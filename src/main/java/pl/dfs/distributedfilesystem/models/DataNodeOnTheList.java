package pl.dfs.distributedfilesystem.models;

public class DataNodeOnTheList {
    public DataNodeOnTheList() {
    }

    public DataNodeOnTheList(String address, String storage) {
        this.address = address;
        this.storage = storage;
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

    private String address;
    private String storage;
}
