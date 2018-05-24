package pl.dfs.distributedfilesystem.controllers;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tomcat.util.buf.StringUtils;
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
import pl.dfs.distributedfilesystem.folders.FoldersRepository;
import pl.dfs.distributedfilesystem.models.DataNodeOnTheList;
import pl.dfs.distributedfilesystem.nodes.DataNodesRepository;
import pl.dfs.distributedfilesystem.files.FilesRepository;
import pl.dfs.distributedfilesystem.files.SingleFile;
import pl.dfs.distributedfilesystem.models.FileOnFileList;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Controller
public class FilesAccessController {

    @Autowired
    DataNodesRepository dataNodesRepository;

    @Autowired
    FilesRepository filesRepository;

    @Autowired
    FoldersRepository foldersRepository;


    @RequestMapping("/")
    public String mainPage(Model model, HttpSession session) {

        if(session.isNew()) {
            session.setAttribute("path","/");
        }

        try {
            session.getAttribute("path");
        }
        catch (Exception e){
            session.setAttribute("path","/");
        }

        List<FileOnFileList> filesOnTheList = new ArrayList<>();

        for(SingleFile singleFile: filesRepository.getFilesByPathWhenExists(session.getAttribute("path").toString())) {
            filesOnTheList.add(new FileOnFileList(singleFile.getName(),singleFile.getSize()));
        }
        model.addAttribute("filesOnTheList",filesOnTheList);
        model.addAttribute("foldersOnTheList",foldersRepository.subfoldersOfFolder(session.getAttribute("path").toString()));
        return "index";
    }

    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file,HttpSession session) {
        if (!file.isEmpty()) {
            if(!filesRepository.checkIfExist(file.getOriginalFilename())) {
                try {
                    byte[] bytes = file.getBytes();
                    String rootPath = System.getProperty("user.home");
                    File dir = new File(rootPath + File.separator + "dsfNameNode");
                    if (!dir.exists())
                        dir.mkdirs();
                    File serverFile = new File(dir.getAbsolutePath()
                            + File.separator + file.getOriginalFilename());
                    BufferedOutputStream stream = new BufferedOutputStream(
                            new FileOutputStream(serverFile));
                    stream.write(bytes);
                    stream.close();
                    try {
                        String address = dataNodesRepository.getLeastOccupiedNode();

                        dataNodesRepository.get(address).writeString("save ");
                        dataNodesRepository.get(address).writeString("\"" + file.getOriginalFilename() + "\" ");
                        dataNodesRepository.get(address).writeFile(serverFile);
                        dataNodesRepository.get(address).writeFlush();

                        serverFile.delete();

                        String response = dataNodesRepository.get(address).readResponse();

                        if (response.equals("success")) {
                            filesRepository.addFile(new SingleFile(file.getOriginalFilename(), bytes.length, session.getAttribute("path").toString(),dataNodesRepository.get(address).getAddress()));
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
        String address = filesRepository.getFileNode(filename);
        dataNodesRepository.get(address).writeString("download ");
        dataNodesRepository.get(address).writeString("\"" + filename + "\" ");
        dataNodesRepository.get(address).writeFlush();

        byte[] toSend = dataNodesRepository.get(address).readResponseBytes();

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
        String address = filesRepository.getFileNode(filename);
        dataNodesRepository.get(address).writeString("delete ");
        dataNodesRepository.get(address).writeString("\"" + filename + "\" ");
        dataNodesRepository.get(address).writeFlush();

        String response = dataNodesRepository.get(address).readResponse();

        if(response.equals("success")) {
            filesRepository.deleteFile(filename);
        }
        return "redirect:/";
    }

    @RequestMapping("/enterFolder")
    public String enterFolder(HttpSession session,@RequestParam String foldername) {
        String currentPath = session.getAttribute("path").toString();
        currentPath +=  foldername + "/";
        session.setAttribute("path",currentPath);
        return "redirect:/";
    }

    @RequestMapping("/addFolder")
    public String addFolder(HttpSession session,@RequestParam String folderName) {
        if(folderName.split(" ").length==1 && folderName.matches("[a-zA-Z0-9]+")) {
            if(!foldersRepository.subfoldersOfFolder(session.getAttribute("path").toString()).contains(folderName)) {
                foldersRepository.addFolder(session.getAttribute("path").toString(),folderName);
            }
        }
        return "redirect:/";
    }

    @RequestMapping("/deleteFolder")
    public String deleteFolder(HttpSession session,@RequestParam String foldername){

        foldersRepository.removeFolder(session.getAttribute("path").toString(),foldername);
        filesRepository.removeFilesByPathWhenExists(session.getAttribute("path").toString()+foldername+"/");

        return "redirect:/";
    }

    @RequestMapping("/goBack")
    public String goBack(HttpSession session) {
        String currentPath = session.getAttribute("path").toString();
        if(!currentPath.equals("/")) {
            int i = currentPath.length()-2;
            while(currentPath.charAt(i)!='/')i--;
            currentPath = currentPath.substring(0,i+1);
            session.setAttribute("path",currentPath);
        }
        return "redirect:/";
    }

    @RequestMapping("/about")
    public String about(Model model){

        ArrayList<DataNodeOnTheList> dataNodeOnTheListArrayList = new ArrayList<>();
        for(int i = 0; i < dataNodesRepository.getNumber();i++) {
            dataNodeOnTheListArrayList.add(new DataNodeOnTheList(dataNodesRepository.get(i).getAddress(),dataNodesRepository.getStorage(dataNodesRepository.get(i).getAddress())));
        }

        model.addAttribute("dataNodesOnTheList",dataNodeOnTheListArrayList);



        return "about";
    }
}
