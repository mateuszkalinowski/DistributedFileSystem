package pl.dfs.distributedfilesystem.filesDatabase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import pl.dfs.distributedfilesystem.dataNodes.DataNodes;

import java.io.*;
import java.util.*;

@Component
public class Files {

    @Autowired
    DataNodes dataNodes;

    public Files(){
        try {
            fileArrayList = new ArrayList<>();
            File nameNodePath = new File(rootPath);

            if (!nameNodePath.exists())
                nameNodePath.mkdir();
            if (!new File(rootPath + File.separator + "filesInformation").exists()) {
                new File(rootPath + File.separator + "filesInformation").createNewFile();
            }
            Scanner in = new Scanner(new File(rootPath + File.separator + "filesInformation"));
            while(in.hasNextLine()) {
                try {
                    String line = in.nextLine();
                    String divided[] = new String[3];
                    int i = 1;
                    while (line.charAt(i) != '"') i++;
                    divided[0] = line.substring(1, i);
                    line = line.substring(i+2,line.length());
                    divided[1] = line.split(" ")[0];
                    divided[2] = line.split(" ")[1];
                    fileArrayList.add(new SingleFile(divided[0],Integer.parseInt(divided[1]),divided[2]));

                }
                catch (Exception e){
                    continue;
                }
            }
        } catch (IOException e) {
            System.out.println("DistributedFileSystem cannot create configuration files!");
        }
    }

    public void addFile(SingleFile singleFile) {
        fileArrayList.add(singleFile);
        writeFilesInformationFile();
    }

    public void deleteFile(String filename) {
        for(int i = fileArrayList.size()-1;i>=0;i--) {
            if(fileArrayList.get(i).getName().equals(filename)) {
                dataNodes.removeOccupiedSpaceFromNode(fileArrayList.get(i).getNode(),fileArrayList.get(i).getSize());
                fileArrayList.remove(i);
            }
        }
        writeFilesInformationFile();
    }

    public void writeFilesInformationFile(){
        try {
            PrintWriter fileInformationWriter = new PrintWriter(new File(rootPath + File.separator + "filesInformation"));
            for(SingleFile singleFile : fileArrayList)
                fileInformationWriter.println("\"" + singleFile.getName() + "\" " + singleFile.getSize() + " " + singleFile.getNode());
            fileInformationWriter.close();
        }
        catch (Exception e) {
            System.out.println("DistributedFileSystem cannot create configuration files!");
        }
    }

    public void initializeDataNodesSizes(){
        for(SingleFile singleFile: fileArrayList) {
            dataNodes.addOccupiedSpaceToNode(singleFile.getNode(),singleFile.getSize());
        }
    }

    public ArrayList<SingleFile> getAllFiles(){
        return fileArrayList;
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

    private ArrayList<SingleFile> fileArrayList;

    String rootPath = System.getProperty("user.home") + File.separator + "nameNode";
}
