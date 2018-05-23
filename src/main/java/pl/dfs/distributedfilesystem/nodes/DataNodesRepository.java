package pl.dfs.distributedfilesystem.nodes;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class DataNodesRepository {

    public DataNodesRepository(){
        try {
            dataNodeArrayList = new ArrayList<>();

            File nameNodePath = new File(rootPath);

            if (!nameNodePath.exists())
                nameNodePath.mkdir();

            File nameNodeFilesInformationPath = new File(rootPath + File.separator + "configuration");

            if (!nameNodeFilesInformationPath.exists())
                nameNodeFilesInformationPath.mkdir();

            if (!new File(rootPath + File.separator + "configuration" + File.separator + "dataNodes").exists()) {
                new File(rootPath + File.separator + "configuration" + File.separator + "dataNodes").createNewFile();
            }
            Scanner in = new Scanner(new File(rootPath + File.separator + "configuration" + File.separator + "dataNodes"));
            while(in.hasNextLine()) {
                try {
                    String line = in.nextLine();
                    String divided[] = line.split(":");
                    dataNodeArrayList.add(new DataNode(divided[0], Integer.parseInt(divided[1])));
                } catch (Exception ignored){}
            }
            for (DataNode dataNode : dataNodeArrayList)
                occupiedSpaceOnNodes.put(dataNode.getAddress(), 0);
        } catch (Exception e) {

        }
    }

    public DataNode get(int i) {
        return dataNodeArrayList.get(i);
    }

    public DataNode get(String address) {
        for(DataNode e : dataNodeArrayList) {
            if(e.getAddress().equals(address))
                return e;
        }
        return null;
    }

    public int getNumber(){
        return dataNodeArrayList.size();
    }

    public boolean addOccupiedSpaceToNode(String address,int storage) {
        if(occupiedSpaceOnNodes.containsKey(address)) {
            int value = occupiedSpaceOnNodes.get(address);
            value+=storage;
            occupiedSpaceOnNodes.put(address,value);
            return true;
        } else
            return false;
    }

    public boolean removeOccupiedSpaceFromNode(String address,int storage) {
        if(occupiedSpaceOnNodes.containsKey(address)) {
            int value = occupiedSpaceOnNodes.get(address);
            value-=storage;
            occupiedSpaceOnNodes.put(address,value);
            return true;
        } else
            return false;
    }

    private ArrayList<DataNode> dataNodeArrayList;

    public void printOccupiedSpace(){
        for(String s : occupiedSpaceOnNodes.keySet())
            System.out.println(s + " " + occupiedSpaceOnNodes.get(s));
    }

    public String getLeastOccupiedNode(){
        String address = null;
        int value = -1;

        for(String key : occupiedSpaceOnNodes.keySet()) {
            if(value==-1) {
                address = key;
                value = occupiedSpaceOnNodes.get(key);
            }
            else {
                if(occupiedSpaceOnNodes.get(key)<value) {
                    value = occupiedSpaceOnNodes.get(key);
                    address = key;
                }
            }
        }
        return address;

    }

    public int getStorage(String address) {
        return occupiedSpaceOnNodes.get(address);
    }



    Map<String,Integer> occupiedSpaceOnNodes = new HashMap<>();

    String rootPath = System.getProperty("user.home") + File.separator + "dsfNameNode";
}
