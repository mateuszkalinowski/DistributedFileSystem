package pl.dfs.distributedfilesystem.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.dfs.distributedfilesystem.files.FilesRepository;
import pl.dfs.distributedfilesystem.nodes.DataNodesRepository;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

@Component
public class ToChangeRepository {

    @Autowired
    DataNodesRepository dataNodesRepository;

    public ToChangeRepository(){
        try {
            File nameNodePath = new File(rootPath);
            toChangeArrayList = new ArrayList<>();
            if (!nameNodePath.exists())
                nameNodePath.mkdir();

            File nameNodeFilesInformationPath = new File(rootPath + File.separator + "files");

            if (!nameNodeFilesInformationPath.exists())
                nameNodeFilesInformationPath.mkdir();

            if (!new File(rootPath + File.separator + "files" + File.separator + "toChangeInformation").exists()) {
                new File(rootPath + File.separator + "files" + File.separator + "toChangeInformation").createNewFile();
            }
            Scanner in = new Scanner(new File(rootPath + File.separator +"files" + File.separator + "toChangeInformation"));
            while(in.hasNextLine()) {
                String line = in.nextLine();
                String divided[] = line.split(",");
                toChangeArrayList.add(new ToChange(divided[0],divided[1],divided[2],divided[3]));
            }
        } catch (Exception e) {

        }
    }

    public void add(ToChange toChange){
        toChangeArrayList.add(toChange);
        writeToFile();
    }

    public void tryToExecuteCommands(){
        ArrayList<Integer> indexesToDelete = new ArrayList<>();
        String address;
        for(int i = 0;i<toChangeArrayList.size();i++) {
            try {
                address = toChangeArrayList.get(i).getAddress();
                dataNodesRepository.get(address).writeString(toChangeArrayList.get(i).getCommand());
                dataNodesRepository.get(address).writeString(toChangeArrayList.get(i).getFrom());
                dataNodesRepository.get(address).writeString(toChangeArrayList.get(i).getTo());
                dataNodesRepository.get(address).writeFlush();
                String response = dataNodesRepository.get(address).readResponse();
                if (response.equals("success")) {
                    indexesToDelete.add(i);
                }
            } catch (Exception e){}
        }

        for(int i = indexesToDelete.size()-1;i>=0;i--) {
            indexesToDelete.remove(indexesToDelete.get(i));
        }
        writeToFile();

    }

    public void writeToFile(){
        try {
            PrintWriter writer = new PrintWriter(new File(rootPath + File.separator + "files" + File.separator + "toChangeInformation"));
            for(int i = 0; i < toChangeArrayList.size();i++){
                writer.println(toChangeArrayList.get(i).getAddress() + "," + toChangeArrayList.get(i).getCommand()+","+toChangeArrayList.get(i).getFrom()+","+toChangeArrayList.get(i).getTo());
            }
            writer.close();
        }
        catch (Exception e){}
    }


    String rootPath = System.getProperty("user.home") + File.separator + "dsfNameNode";

    private ArrayList<ToChange> toChangeArrayList;
}
