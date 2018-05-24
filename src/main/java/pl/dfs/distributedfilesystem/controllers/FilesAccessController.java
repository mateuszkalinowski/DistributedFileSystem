package pl.dfs.distributedfilesystem.controllers;

import javafx.util.Pair;
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
import pl.dfs.distributedfilesystem.models.ObjectOnTheList;
import pl.dfs.distributedfilesystem.nodes.DataNodesRepository;
import pl.dfs.distributedfilesystem.files.FilesRepository;
import pl.dfs.distributedfilesystem.files.SingleFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
            session.setAttribute("error","");
        }

        try {
            session.getAttribute("path");
            session.getAttribute("error");
        }
        catch (Exception e){
            session.setAttribute("path","/");
            session.setAttribute("error","");
        }

        List<ObjectOnTheList> objectsOnTheList = new ArrayList<>();

        for(SingleFile singleFile: filesRepository.getFilesByPathWhenExists(session.getAttribute("path").toString())) {
            double size = singleFile.getSize();
            String unit = "B";
            if(size > 1000 && size <1000000) {
                size = size / 1000.0;
                size = Math.round(size * 100.0)/100.0;
                unit = "KB";
            }
            else if(size > 1000000) {
                size = size / 1000000.0;
                size = Math.round(size * 100.0)/100.0;
                unit = "MB";
            }

            String sizeString = String.valueOf(size);
            sizeString = sizeString.replaceFirst(".0","");

            objectsOnTheList.add(new ObjectOnTheList(singleFile.getName(),sizeString + " " + unit,String.valueOf(singleFile.getNode().split(",").length),"file"));
        }

        for(String key : foldersRepository.subfoldersOfFolder(session.getAttribute("path").toString())) {
            objectsOnTheList.add(new ObjectOnTheList(key,"-","-","folder"));
        }

        if(session.getAttribute("error").equals("badFolderName")) {
            session.setAttribute("error","");
            model.addAttribute("error","badFolderName");
        } else if(session.getAttribute("error").equals("fileAlreadyExists")) {
            session.setAttribute("error","");
            model.addAttribute("error","fileAlreadyExists");
        } else if(session.getAttribute("error").equals("zeroNodes")) {
            session.setAttribute("error","");
            model.addAttribute("error","zeroNodes");
        } else if(session.getAttribute("error").equals("noFileToSend")) {
            session.setAttribute("error","");
            model.addAttribute("error","noFileToSend");
        }
        else {
            model.addAttribute("error","");
        }

        int numberOfNodesForReplication = dataNodesRepository.getNumber();
        ArrayList<String> wartosci = new ArrayList<>();
        for(int i = 0; i < numberOfNodesForReplication;i++) {
            wartosci.add(String.valueOf((i+1)));
        }

        model.addAttribute("replicationValues",wartosci);
        model.addAttribute("objectsOnTheList",objectsOnTheList);

        //model.addAttribute("foldersOnTheList",foldersRepository.subfoldersOfFolder(session.getAttribute("path").toString()));

        return "index";
    }

    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file,HttpSession session,HttpServletRequest request) {

        if(dataNodesRepository.getNumber()!=0) {

            if (!file.isEmpty()) {
                if (!filesRepository.checkIfExist(file.getOriginalFilename())) {
                    try {
                      // System.out.println(request.getParameter("replication"));
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
                            ArrayList<String> addresses = dataNodesRepository.getLeastOccupiedNodes(Integer.parseInt(request.getParameter("replication")));
                            String finalAddressesLine = "";
                            for (String address : addresses) {
                                dataNodesRepository.get(address).writeString("save ");
                                dataNodesRepository.get(address).writeString("\"" + file.getOriginalFilename() + "\" ");
                                dataNodesRepository.get(address).writeFile(serverFile);
                                dataNodesRepository.get(address).writeFlush();
                                String response = dataNodesRepository.get(address).readResponse();
                                if (response.equals("success")) {
                                    finalAddressesLine+=address+",";
                                }
                        }
                            if (finalAddressesLine.length()>0) {
                                filesRepository.addFile(new SingleFile(file.getOriginalFilename(), bytes.length, session.getAttribute("path").toString(), finalAddressesLine));
                            }
                            serverFile.delete();

                        } catch (Exception ignored) {
                            serverFile.delete();
                        }

                    } catch (Exception ignored) {
                        System.out.println(ignored.getMessage());
                    }
                } else {
                    session.setAttribute("error", "fileAlreadyExists");
                }
            } else {
                session.setAttribute("error", "noFileToSend");
            }
        }
        else {
            session.setAttribute("error", "zeroNodes");
        }
        return "redirect:/";
    }

    @RequestMapping("/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String filename) {
        String addresses = filesRepository.getFileNode(filename);

        String address = "";

        for (String key : addresses.split(",")) {
            if (dataNodesRepository.get(key) != null) {
                address = key;
                break;
            }
        }

        if(address.equals("")) {
            return new ResponseEntity("Nie można było przetworzyc żądania", HttpStatus.INTERNAL_SERVER_ERROR);
        }

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

    }

    @RequestMapping("/deleteFile")
    public String fileDelete(@RequestParam String filename) {
        String addresses = filesRepository.getFileNode(filename);
        ArrayList<String> divided = new ArrayList<>(Arrays.asList(addresses.split(",")));
        for(int i = divided.size()-1;i>=0;i--) {
            String address = divided.get(i);
            try {
                dataNodesRepository.get(address).writeString("delete ");
                dataNodesRepository.get(address).writeString("\"" + filename + "\" ");
                dataNodesRepository.get(address).writeFlush();
                String response = dataNodesRepository.get(address).readResponse();
                if (response.equals("success")) {
                    divided.remove(i);
                }
            } catch (Exception e){
                filesRepository.toDelete.add(new Pair<>(filename,address));
                filesRepository.rewriteFileToDelete();
            }
        }

        filesRepository.deleteFile(filename);
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
        else {
            session.setAttribute("error","badFolderName");
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

            double size = dataNodesRepository.getStorage(dataNodesRepository.get(i).getAddress());
            String unit = "B";
            if(size > 1000 && size <1000000) {
                size = size / 1000.0;
                size = Math.round(size * 100.0)/100.0;
                unit = "KB";
            }
            else if(size > 1000000) {
                size = size / 1000000.0;
                size = Math.round(size * 100.0)/100.0;
                unit = "MB";
            }

            String sizeString = String.valueOf(size);
            sizeString = sizeString.replaceFirst(".0","");

                dataNodesRepository.get(dataNodesRepository.get(i).getAddress()).writeString("freespace ");
                dataNodesRepository.get(dataNodesRepository.get(i).getAddress()).writeFlush();
                String answer = dataNodesRepository.get(dataNodesRepository.get(i).getAddress()).readResponse();

                double freeSpace = Double.parseDouble(answer.trim());
                String unitFreeSpace = "B";

                if(freeSpace > 1000 && freeSpace < 1000000) {
                    freeSpace = freeSpace / 1000.0;
                    freeSpace = Math.round(freeSpace * 100.0)/100.0;
                    unitFreeSpace = "KB";
                }
                else if(freeSpace > 1000000) {
                    freeSpace = freeSpace / 1000000.0;
                    freeSpace = Math.round(freeSpace * 100.0)/100.0;
                    unitFreeSpace = "MB";
                }

                dataNodeOnTheListArrayList.add(new DataNodeOnTheList(dataNodesRepository.get(i).getAddress(),sizeString + " " + unit ,freeSpace + " " + unitFreeSpace));
        }

        model.addAttribute("dataNodesOnTheList",dataNodeOnTheListArrayList);



        return "about";
    }
}
