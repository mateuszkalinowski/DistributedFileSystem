package pl.dfs.distributedfilesystem.nodes;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

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

    public int getStorage(String address) {
        return occupiedSpaceOnNodes.get(address);
    }

    public ArrayList<String> getLeastOccupiedNodes(int howMany){
        ArrayList<String> toReturn = new ArrayList<>();
        LinkedHashMap<String,Integer> map = sortHashMapByValues(occupiedSpaceOnNodes);
        int i = 0;
        for(String key : map.keySet()) {
            if(i<howMany && i < dataNodeArrayList.size()) {
                i++;
                toReturn.add(key);
            }
            else
                break;
        }
        return toReturn;
    }


    HashMap<String,Integer> occupiedSpaceOnNodes = new HashMap<>();

    String rootPath = System.getProperty("user.home") + File.separator + "dsfNameNode";

    private LinkedHashMap<String, Integer> sortHashMapByValues(
            HashMap<String, Integer> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, Integer> sortedMap =
                new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Integer val = valueIt.next();
            Iterator<String > keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String  key = keyIt.next();
                Integer comp1 = passedMap.get(key);
                Integer comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

}
