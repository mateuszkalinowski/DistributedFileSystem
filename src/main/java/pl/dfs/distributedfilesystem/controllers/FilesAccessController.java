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
import java.lang.reflect.Array;
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

        checkSession(session);

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

            String icon = "-";

            if(singleFile.getName().endsWith(".avi"))
                icon = "/fileIcons/avi.png";
            else if(singleFile.getName().endsWith(".css"))
                icon = "/fileIcons/css.png";
            else if(singleFile.getName().endsWith(".csv"))
                icon = "/fileIcons/csv.png";
            else if(singleFile.getName().endsWith(".dbf"))
                icon = "/fileIcons/dbf.png";
            else if(singleFile.getName().endsWith(".doc") || singleFile.getName().endsWith(".docx"))
                icon = "/fileIcons/doc.png";
            else if(singleFile.getName().endsWith(".dwg"))
                icon = "/fileIcons/dwg.png";
            else  if(singleFile.getName().endsWith(".exe"))
                icon = "/fileIcons/exe.png";
            else if(singleFile.getName().endsWith(".fla"))
                icon = "/fileIcons/fla.png";
            else if(singleFile.getName().endsWith(".html"))
                icon = "/fileIcons/html.png";
            else if(singleFile.getName().endsWith(".iso"))
                icon = "/fileIcons/iso.png";
            else if(singleFile.getName().endsWith(".js"))
                icon = "/fileIcons/javascript.png";
            else if(singleFile.getName().endsWith(".json"))
                icon = "/fileIcons/json-file.png";
            else if(singleFile.getName().endsWith(".jpg"))
                icon = "/fileIcons/jpg.png";
            else if(singleFile.getName().endsWith(".mp3"))
                icon = "/fileIcons/mp3.png";
            else if(singleFile.getName().endsWith(".mp4"))
                icon = "/fileIcons/mp4.png";
            else if(singleFile.getName().endsWith(".pdf"))
                icon = "/fileIcons/pdf.png";
            else if(singleFile.getName().endsWith(".psd"))
                icon = "/fileIcons/psd.png";
            else if(singleFile.getName().endsWith(".png"))
                icon = "/fileIcons/png.png";
            else if(singleFile.getName().endsWith(".ppt") || singleFile.getName().endsWith(".pptx"))
                icon = "/fileIcons/ppt.png";
            else if(singleFile.getName().endsWith(".rft"))
                icon = "/fileIcons/rft.png";
            else if(singleFile.getName().endsWith(".svg"))
                icon = "/fileIcons/svg.png";
            else if(singleFile.getName().endsWith(".txt"))
                icon = "/fileIcons/txt.png";
            else if(singleFile.getName().endsWith(".xls") || singleFile.getName().endsWith(".xlsx"))
                icon = "/fileIcons/xls.png";
            else if(singleFile.getName().endsWith(".xml"))
                icon = "/fileIcons/xml.png";
            else if(singleFile.getName().endsWith(".zip"))
                icon = "/fileIcons/zip.png";
            else
                icon = "/fileIcons/file.png";
            objectsOnTheList.add(new ObjectOnTheList(singleFile.getName(),sizeString + " " + unit,String.valueOf(singleFile.getNode().split(",").length),"file",icon));
        }
        for(String key : foldersRepository.subfoldersOfFolder(session.getAttribute("path").toString())) {
            objectsOnTheList.add(new ObjectOnTheList(key,"-","-","folder","/fileIcons/folder.png"));
        }
        if(!session.getAttribute("path").equals("/"))
            objectsOnTheList.add(new ObjectOnTheList("..","-","-","folder","/fileIcons/backfolder.png"));

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
        } else if(session.getAttribute("error").equals("notEnoughSpace")) {
            session.setAttribute("error","");
            model.addAttribute("error","notEnoughSpace");
        } else if(session.getAttribute("error").equals("fileNotExist")) {
            session.setAttribute("error","");
            model.addAttribute("error","fileNotExist");
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
        return "index";
    }

    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file,HttpSession session,HttpServletRequest request) {
        checkSession(session);
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
                            else {
                                session.setAttribute("error", "notEnoughSpace");
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
    public ResponseEntity downloadFile(@RequestParam String filename,HttpSession session) {
        checkSession(session);
        String addresses = filesRepository.getFileNode(filename);
        System.out.println(filename);
        byte[] toSend = new byte[100*1024*1024];
        boolean exists = false;
        ArrayList<String> addressesArrayList = new ArrayList<>(Arrays.asList(addresses.split(",")));
        for(int i = addressesArrayList.size()-1; i>=0;i--) {

            String address = addressesArrayList.get(i);

            dataNodesRepository.get(address).writeString("download ");
            dataNodesRepository.get(address).writeString("\"" + filename + "\" ");
            dataNodesRepository.get(address).writeFlush();

            try {
                toSend = dataNodesRepository.get(address).readResponseBytes();
                exists=true;
                break;
            } catch (FileNotFoundException e) {
                addressesArrayList.remove(i);
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        StringBuilder newAdresses = new StringBuilder();
        for(int i = 0 ; i < addressesArrayList.size();i++) {
            newAdresses.append(addressesArrayList.get(i)).append(",");
        }

        for(int i = 0; i < filesRepository.getAllFiles().size();i++) {
            if(filesRepository.getAllFiles().get(i).getName().equals(filename)) {
                if(!newAdresses.toString().trim().equals("")) {
                    filesRepository.getAllFiles().get(i).setNode(newAdresses.toString());
                    filesRepository.writeFilesInformationFile();
                }
                else {
                    filesRepository.deleteFile(filename);
                }
            }
        }

        if(exists) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("charset", "utf-8");
            responseHeaders.setContentType(MediaType.valueOf("text/html"));
            responseHeaders.setContentLength(toSend.length);
            responseHeaders.set("Content-disposition", "attachment; filename=" + filename);
            return new ResponseEntity(toSend, responseHeaders, HttpStatus.OK);
        }
        else {
            session.setAttribute("error","fileNotExist");
            return new ResponseEntity("<script>window.location.replace(\"/\");</script>",HttpStatus.INTERNAL_SERVER_ERROR);

        }
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
        checkSession(session);
        String currentPath = session.getAttribute("path").toString();
        if(foldername.equals("..")) {
            int index = currentPath.length()-2;
            while(currentPath.charAt(index-1)!='/')index--;
            currentPath = currentPath.substring(0,index);
        }
        else {
            currentPath += foldername + "/";
        }
        session.setAttribute("path", currentPath);
        return "redirect:/";
    }

    @RequestMapping("/addFolder")
    public String addFolder(HttpSession session,@RequestParam String folderName) {
        checkSession(session);
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
        checkSession(session);

        foldersRepository.removeFolder(session.getAttribute("path").toString(),foldername);
        filesRepository.removeFilesByPathWhenExists(session.getAttribute("path").toString()+foldername+"/");

        return "redirect:/";
    }

    @RequestMapping("/goBack")
    public String goBack(HttpSession session) {
        checkSession(session);
        String currentPath = session.getAttribute("path").toString();
        if(!currentPath.equals("/")) {
            int i = currentPath.length()-2;
            while(currentPath.charAt(i)!='/')i--;
            currentPath = currentPath.substring(0,i+1);
            session.setAttribute("path",currentPath);
        }
        return "redirect:/";
    }

    @RequestMapping("/changeFolder")
    public String changeFolder(@RequestParam String file,@RequestParam String folder,HttpSession session) {

        String path = session.getAttribute("path").toString();

        for(int i = 0; i < filesRepository.getAllFiles().size();i++) {
            if(filesRepository.getAllFiles().get(i).getName().equals(file)) {
                if(folder.equals("..")) {

                    String currentPath = filesRepository.getAllFiles().get(i).getPath();

                    int index = currentPath.length()-2;
                    while(currentPath.charAt(index-1)!='/')index--;
                    currentPath = currentPath.substring(0,index);

                    filesRepository.getAllFiles().get(i).setPath(currentPath);
                }
                else {
                    filesRepository.getAllFiles().get(i).setPath(path + folder + File.separator);
                }
            }
        }
        filesRepository.writeFilesInformationFile();
        return "redirect:/";
    }

    @RequestMapping("/about")
    public String about(Model model){
        ArrayList<DataNodeOnTheList> dataNodeOnTheListArrayList = new ArrayList<>();
        for(int i = 0; i < dataNodesRepository.getNumber();i++) {

            double size = dataNodesRepository.getStorage(dataNodesRepository.get(i).getAddress());
            String unit = "B";
            if(size > 1000 && size < 1000000) {
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

    private void checkSession(HttpSession session){
        if(session.isNew()) {
            session.setAttribute("path","/");
            session.setAttribute("error","");
        }
    }
}
