package pl.dfs.distributedfilesystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.dfs.distributedfilesystem.dataNodes.DataNode;
import pl.dfs.distributedfilesystem.dataNodes.DataNodes;
import pl.dfs.distributedfilesystem.filesDatabase.Files;
import pl.dfs.distributedfilesystem.filesDatabase.SingleFile;
import pl.dfs.distributedfilesystem.models.FileOnFileList;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


@Controller
public class FilesAccessController {

    @Autowired
    DataNodes dataNodes;

    @Autowired
    Files files;


    @RequestMapping("/")
    public String mainPage(Model model) {

        List<FileOnFileList> filesOnTheList = new ArrayList<>();

        for(SingleFile singleFile:files.getAllFiles()) {
            filesOnTheList.add(new FileOnFileList(singleFile.getName(),singleFile.getSize()));
        }
        model.addAttribute("filesOnTheList",filesOnTheList);
        return "index";
    }

    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            if(!files.checkIfExist(file.getOriginalFilename())) {
                try {
                    byte[] bytes = file.getBytes();
                    String rootPath = System.getProperty("user.home");
                    File dir = new File(rootPath + File.separator + "nameNode");
                    if (!dir.exists())
                        dir.mkdirs();
                    File serverFile = new File(dir.getAbsolutePath()
                            + File.separator + file.getOriginalFilename());
                    BufferedOutputStream stream = new BufferedOutputStream(
                            new FileOutputStream(serverFile));
                    stream.write(bytes);
                    stream.close();
                    try {
                        String address = dataNodes.getLeastOccupiedNode();

                        dataNodes.get(address).writeString("save ");
                        dataNodes.get(address).writeString("\"" + file.getOriginalFilename() + "\" ");
                        dataNodes.get(address).writeFile(serverFile);
                        dataNodes.get(address).writeFlush();

                        serverFile.delete();

                        String response = dataNodes.get(address).readResponse();

                        if (response.equals("success")) {
                            files.addFile(new SingleFile(file.getOriginalFilename(), bytes.length, dataNodes.get(address).getAddress()));
                        }

                    } catch (Exception ignored) {
                        System.out.println(ignored.getMessage());
                    }

                } catch (Exception ignored) {
                    System.out.println(ignored.getMessage());
                }
            }
        }
        return "redirect:/";
    }

    @RequestMapping("/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String filename) {
        String address = files.getFileNode(filename);
        dataNodes.get(address).writeString("download ");
        dataNodes.get(address).writeString("\"" + filename + "\" ");
        dataNodes.get(address).writeFlush();

        byte[] toSend = dataNodes.get(address).readResponseBytes();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("charset", "utf-8");
        responseHeaders.setContentType(MediaType.valueOf("text/html"));
        responseHeaders.setContentLength(toSend.length);
        responseHeaders.set("Content-disposition", "attachment; filename=" + filename);

        return new ResponseEntity<byte[]>(toSend, responseHeaders, HttpStatus.OK);

       // return "redirect:/";
    }

    @RequestMapping("/deleteFile")
    public String fileDelete(@RequestParam String filename) {
        String address = files.getFileNode(filename);
        dataNodes.get(address).writeString("delete ");
        dataNodes.get(address).writeString("\"" + filename + "\" ");
        dataNodes.get(address).writeFlush();

        String response = dataNodes.get(address).readResponse();

        if(response.equals("success")) {
            files.deleteFile(filename);
        }
        return "redirect:/";
    }
}
