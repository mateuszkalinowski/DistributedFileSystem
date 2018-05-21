package pl.dfs.distributedfilesystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.dfs.distributedfilesystem.dataNodes.DataNode;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


@Controller
public class FilesAccessController {


//    Socket sock;
//    String server = "localhost";
//    int port = 4444;
    String command = "hello world";

    @Autowired
    ArrayList<DataNode> dataNodes;


    @RequestMapping("/")
    public String mainPage() {
        return "index";
    }

    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String rootPath = System.getProperty("user.home");
                File dir = new File(rootPath + File.separator + "tmpFiles");
                if (!dir.exists())
                    dir.mkdirs();
                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + file.getOriginalFilename());
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();
                try {
                    dataNodes.get(0).writeString( "save ");
                    dataNodes.get(0).writeString(file.getOriginalFilename()+ " ");
                    dataNodes.get(0).writeFile(serverFile);
                    dataNodes.get(0).writeFlush();

                    System.out.println(dataNodes.get(0).readRespnse());
                }
                catch (Exception ignored) {
                    System.out.println(ignored.getMessage());
                }

            } catch (Exception ignored) {

            }
        }
        return "redirect:/";
    }

    @RequestMapping("/test")
    public String test(){

        return "redirect:/";
    }
}
