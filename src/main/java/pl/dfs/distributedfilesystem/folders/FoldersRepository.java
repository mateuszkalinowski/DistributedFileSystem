package pl.dfs.distributedfilesystem.folders;

import javafx.util.Pair;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

@Component
public class FoldersRepository {

    public FoldersRepository(){
        try {
            File nameNodePath = new File(rootPath);

            if (!nameNodePath.exists())
                nameNodePath.mkdir();

            File nameNodeFilesInformationPath = new File(rootPath + File.separator + "files");

            if (!nameNodeFilesInformationPath.exists())
                nameNodeFilesInformationPath.mkdir();

            if (!new File(rootPath + File.separator + "files" + File.separator + "foldersInformation").exists()) {
                new File(rootPath + File.separator + "files" + File.separator + "foldersInformation").createNewFile();
                PrintWriter out = new PrintWriter(new File(rootPath + File.separator + "files" + File.separator + "foldersInformation"));
                out.write("/");
                out.close();
            }
            foldersMap= new HashMap<>();
            Scanner in = new Scanner(new File(rootPath + File.separator + "files" + File.separator + "foldersInformation"));
            while(in.hasNextLine()) {
                String line = in.nextLine();
                String divided[] = line.split(" ");
                if(divided.length==1) {
                    foldersMap.put(divided[0], "");
                }
                else if(divided.length==2) {
                    foldersMap.put(divided[0],divided[1]);
                }

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rewriteFolders(){
        try {
            File nameNodePath = new File(rootPath);

            if (!nameNodePath.exists())
                nameNodePath.mkdir();

            File nameNodeFilesInformationPath = new File(rootPath + File.separator + "files");

            if (!nameNodeFilesInformationPath.exists())
                nameNodeFilesInformationPath.mkdir();

            if (!new File(rootPath + File.separator + "files" + File.separator + "foldersInformation").exists()) {
                new File(rootPath + File.separator + "files" + File.separator + "foldersInformation").createNewFile();
            }

            PrintWriter writer = new PrintWriter(new File(rootPath + File.separator + "files" + File.separator + "foldersInformation"));
            for(String key : foldersMap.keySet()) {
                writer.println(key + " " + foldersMap.get(key));

            }
            writer.close();
        } catch (Exception ignored){}

    }

    public void addFolder(String path,String folder) {
        String folderList = foldersMap.get(path);
        folderList += folder + ",";
        foldersMap.put(path,folderList);
        foldersMap.put(path+folder+"/","");
        rewriteFolders();
    }

    public void removeFolder(String path,String folder) {
        String folderList = foldersMap.get(path);
        ArrayList<String> folders = new ArrayList<>(Arrays.asList(foldersMap.get(path).split(",")));
        folders.remove(folder);
        folderList = "";
        for(String oneFolder : folders)
            folderList +=oneFolder+",";
        foldersMap.put(path,folderList);
        ArrayList<String> toDelete = new ArrayList<>();
        for(String key : foldersMap.keySet()) {
            if(key.startsWith(path+folder+"/"))
                toDelete.add(key);
        }
        for(String key : toDelete) {
            foldersMap.remove(key);
        }
        rewriteFolders();
    }

    public ArrayList<String> subfoldersOfFolder(String path) {
        if(foldersMap.get(path).equals(""))
            return new ArrayList<>();
        else
            return new ArrayList<>(Arrays.asList(foldersMap.get(path).split(",")));
    }

    public boolean checkIfPathExist(String path) {
        if(foldersMap.get(path)!=null)
            return true;
        else
            return false;
    }

    String rootPath = System.getProperty("user.home") + File.separator + "dsfNameNode";
    private Map<String,String> foldersMap;
}
