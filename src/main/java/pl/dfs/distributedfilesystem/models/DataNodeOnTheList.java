package pl.dfs.distributedfilesystem.models;

public class DataNodeOnTheList {
    public DataNodeOnTheList() {
    }

    public DataNodeOnTheList(String address, int storage) {
        this.address = address;
        this.storage = storage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    private String address;
    private int storage;
}
