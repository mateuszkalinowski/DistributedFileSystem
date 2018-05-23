package pl.dfs.distributedfilesystem.dataNodes;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataNodes {

    public DataNodes(){
        dataNodeArrayList = new ArrayList<>();
        try {
            dataNodeArrayList.add(new DataNode("localhost", 4444));
        }
        catch (Exception e) {

        }
        try {
            dataNodeArrayList.add(new DataNode("localhost", 4445));
        }
        catch (Exception e) {

        }

        for(DataNode dataNode : dataNodeArrayList)
            occupiedSpaceOnNodes.put(dataNode.getAddress(),0);
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

    Map<String,Integer> occupiedSpaceOnNodes = new HashMap<>();
}
