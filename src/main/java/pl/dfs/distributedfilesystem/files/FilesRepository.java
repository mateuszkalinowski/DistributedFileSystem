package pl.dfs.distributedfilesystem.files;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            dataNodesRepository.addOccupiedSpaceToNode(singleFile.getNode(),singleFile.getSize());
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
            if(singleFile.getPath().equals(path))
                if(dataNodesRepository.get(singleFile.getNode())!=null)
                    filesFromLocation.add(singleFile);
        }
        return filesFromLocation;
    }

    public void removeFilesByPathWhenExists(String path) {
        for(int i = fileArrayList.size()-1;i>=0;i--) {
            if(dataNodesRepository.get(fileArrayList.get(i).getNode())!=null) {
                if(fileArrayList.get(i).getPath().startsWith(path)) {
                    String address = fileArrayList.get(i).getNode();
                    dataNodesRepository.get(address).writeString("delete ");
                    dataNodesRepository.get(address).writeString("\"" + fileArrayList.get(i).getName() + "\" ");
                    dataNodesRepository.get(address).writeFlush();

                    String response = dataNodesRepository.get(address).readResponse();

                    if(response.equals("success")) {
                        dataNodesRepository.removeOccupiedSpaceFromNode(fileArrayList.get(i).getNode(),fileArrayList.get(i).getSize());
                        fileArrayList.remove(i);
                    }
                }
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
                    String address = fileArrayList.get(i).getNode();
                    dataNodesRepository.get(address).writeString("delete ");
                    dataNodesRepository.get(address).writeString("\"" + fileArrayList.get(i).getName() + "\" ");
                    dataNodesRepository.get(address).writeFlush();

                    String response = dataNodesRepository.get(address).readResponse();

                    if(response.equals("success")) {
                        dataNodesRepository.removeOccupiedSpaceFromNode(fileArrayList.get(i).getNode(),fileArrayList.get(i).getSize());
                        fileArrayList.remove(i);
                    }
                }
            }
        }
        writeFilesInformationFile();
    }


    private ArrayList<SingleFile> fileArrayList;

    String rootPath = System.getProperty("user.home") + File.separator + "dsfNameNode";
}
