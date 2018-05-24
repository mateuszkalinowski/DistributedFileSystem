package pl.dfs.distributedfilesystem.models;

public class DataNodeOnTheList {
    public DataNodeOnTheList() {
    }

    public DataNodeOnTheList(String address, String storage,String freeStorage) {
        this.address = address;
        this.storage = storage;
        this.freeStorage = freeStorage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getFreeStorage() {
        return freeStorage;
    }

    public void setFreeStorage(String freeStorage) {
        this.freeStorage = freeStorage;
    }

    private String address;
    private String storage;
    private String freeStorage;
}
