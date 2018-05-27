package pl.dfs.distributedfilesystem.files;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.dfs.distributedfilesystem.folders.FoldersRepository;
import pl.dfs.distributedfilesystem.nodes.DataNodesRepository;

import java.io.*;
import java.util.*;

@Component
public class FilesRepository {

    @Autowired
    DataNodesRepository dataNodesRepository;

    @Autowired
    FoldersRepository foldersRepository;

    public FilesRepository(){
        try {
            fileArrayList = new ArrayList<>();
            File nameNodePath = new File(rootPath);

            if (!nameNodePath.exists())
                nameNodePath.mkdir();

            File nameNodeFilesInformationPath = new File(rootPath + File.separator + "files");

            if(!nameNodeFilesInformationPath.exists())
                nameNodeFilesInformationPath.mkdir();

            if (!new File(rootPath + File.separator + "files" + File.separator + "filesInformation").exists()) {
                new File(rootPath + File.separator + "files" + File.separator +"filesInformation").createNewFile();
            }
            Scanner in = new Scanner(new File(rootPath + File.separator +"files" + File.separator + "filesInformation"));
            while(in.hasNextLine()) {
                try {
                    String line = in.nextLine();
                    String divided[] = new String[4];
                    int i = 1;
                    while (line.charAt(i) != '"') i++;
                    divided[0] = line.substring(1, i);
                    line = line.substring(i+2,line.length());
                    divided[1] = line.split(" ")[0];
                    divided[2] = line.split(" ")[1];
                    divided[3] = line.split(" ")[2];
                    fileArrayList.add(new SingleFile(divided[0],Integer.parseInt(divided[1]),divided[2],divided[3]));

                }
                catch (Exception e){
                    continue;
                }
            }

            if (!new File(rootPath + File.separator + "files" + File.separator + "filesToDelete").exists()) {
                new File(rootPath + File.separator + "files" + File.separator +"filesToDelete").createNewFile();
            }

            Scanner in2 = new Scanner(new File(rootPath + File.separator +"files" + File.separator + "filesToDelete"));
            while(in2.hasNextLine()) {
                String line = in2.nextLine();
                int index = line.length()-1;
                while(line.charAt(index)!=' ')index--;

                String name = line.substring(0,index);
                String node = line.substring(index+1,line.length());
                toDelete.add(new Pair<>(name,node));
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void rewriteFileToDelete(){
        try {
            PrintWriter fileInformationWriter = new PrintWriter(new File(rootPath + File.separator + "files" + File.separator + "filesToDelete"));
            for(Pair<String,String> toWrite:toDelete)
                fileInformationWriter.println(toWrite.getKey()+" " + toWrite.getValue());
            fileInformationWriter.close();
        }catch (Exception e){}

    }

    public void addFile(SingleFile singleFile) {
        fileArrayList.add(singleFile);
        dataNodesRepository.addOccupiedSpaceToNode(singleFile.getNode(),singleFile.getSize());
        writeFilesInformationFile();
    }

    public void deleteFile(String filename) {
        for(int i = fileArrayList.size()-1;i>=0;i--) {
            if(fileArrayList.get(i).getName().equals(filename)) {
                dataNodesRepository.removeOccupiedSpaceFromNode(fileArrayList.get(i).getNode(),fileArrayList.get(i).getSize());
                fileArrayList.remove(i);
            }
        }
        writeFilesInformationFile();
    }

    public void writeFilesInformationFile(){
        try {
            PrintWriter fileInformationWriter = new PrintWriter(new File(rootPath + File.separator + "files" + File.separator+  "filesInformation"));
            for(SingleFile singleFile : fileArrayList)
                fileInformationWriter.println("\"" + singleFile.getName() + "\" " + singleFile.getSize() + " " + singleFile.getPath()+ " " + singleFile.getNode());
            fileInformationWriter.close();
        }
        catch (Exception e) {
            System.out.println("DistributedFileSystem cannot create configuration files! (writeFilesInformationFile)");
        }
    }

    public void initializeDataNodesSizes(){
        for(SingleFile singleFile: fileArrayList) {
            for(String node : singleFile.getNode().split(","))
                dataNodesRepository.addOccupiedSpaceToNode(node,singleFile.getSize());
        }
    }

    public ArrayList<SingleFile> getAllFiles(){
        return fileArrayList;
    }

    public ArrayList<SingleFile> getFilesByPath(String path){
        ArrayList<SingleFile> filesFromLocation = new ArrayList<>();
        for(SingleFile singleFile : fileArrayList) {
            if(singleFile.getPath().equals(path))
                filesFromLocation.add(singleFile);
        }
        return filesFromLocation;
    }

    public ArrayList<SingleFile> getFilesByPathWhenExists(String path){
        ArrayList<SingleFile> filesFromLocation = new ArrayList<>();
        for(SingleFile singleFile : fileArrayList) {
            if(singleFile.getPath().equals(path)){
                boolean exists = false;
                for(String key : singleFile.getNode().split(",")) {
                    if(dataNodesRepository.get(key)!=null) {
                        exists = true;
                        break;
                    }
                }
                if(exists)
                    filesFromLocation.add(singleFile);
            }

        }
        return filesFromLocation;
    }

    public void removeFilesByPathWhenExists(String path) {
        for(int i = fileArrayList.size()-1;i>=0;i--) {
                if(fileArrayList.get(i).getPath().startsWith(path)) {
                    String addresses = fileArrayList.get(i).getNode();

                    for(String address : addresses.split(",")) {
                        try {
                            dataNodesRepository.get(address).writeString("delete ");
                            dataNodesRepository.get(address).writeString("\"" + fileArrayList.get(i).getName() + "\" ");
                            dataNodesRepository.get(address).writeFlush();

                            String response = dataNodesRepository.get(address).readResponse();
                            if (response.equals("success")) {
                                dataNodesRepository.removeOccupiedSpaceFromNode(fileArrayList.get(i).getNode(), fileArrayList.get(i).getSize());
                            }
                        } catch (Exception ignored){
                            toDelete.add(new Pair<>(fileArrayList.get(i).getName(),address));
                            rewriteFileToDelete();
                        }
                    }
                    fileArrayList.remove(i);
                }
        }
        writeFilesInformationFile();
    }

    public boolean checkIfExist(String filename){
        for(SingleFile singleFile : fileArrayList) {
            if(singleFile.getName().equals(filename))
                return true;
        }
        return false;
    }

    public String getFileNode(String filename) {
        for(SingleFile singleFile : fileArrayList) {
            if(singleFile.getName().equals(filename))
                return singleFile.getNode();
        }
        return null;
    }

    public void checkCohesion(){
        for(int i = fileArrayList.size()-1;i>=0;i--) {
            if(!foldersRepository.checkIfPathExist(fileArrayList.get(i).getPath())) {
                if(dataNodesRepository.get(fileArrayList.get(i).getNode())!=null) {
                    String addresses = fileArrayList.get(i).getNode();

                    for(String address : addresses.split(",")) {
                        try {
                        dataNodesRepository.get(address).writeString("delete ");
                        dataNodesRepository.get(address).writeString("\"" + fileArrayList.get(i).getName() + "\" ");
                        dataNodesRepository.get(address).writeFlush();

                        String response = dataNodesRepository.get(address).readResponse();
                        if(response.equals("success")) {
                            dataNodesRepository.removeOccupiedSpaceFromNode(fileArrayList.get(i).getNode(),fileArrayList.get(i).getSize());
                        }
                        } catch (Exception e) {
                            toDelete.add(new Pair<>(fileArrayList.get(i).getName(),address));
                            rewriteFileToDelete();
                        }

                    }
                    fileArrayList.remove(i);
                }
            }
        }
        writeFilesInformationFile();
    }

    public void tryToDelete() {

        for(int i = toDelete.size()-1;i>=0;i--) {
            Pair<String,String> key = toDelete.get(i);
            try {
                dataNodesRepository.get(key.getValue()).writeString("delete ");
                dataNodesRepository.get(key.getValue()).writeString(key.getKey()+" ");
                dataNodesRepository.get(key.getValue()).writeFlush();
                toDelete.remove(i);
                rewriteFileToDelete();

            } catch (Exception e) {}
        }
        rewriteFileToDelete();

    }

    private ArrayList<SingleFile> fileArrayList;

    String rootPath = System.getProperty("user.home") + File.separator + "dsfNameNode";

    public ArrayList<Pair<String,String>> toDelete = new ArrayList<>();
}