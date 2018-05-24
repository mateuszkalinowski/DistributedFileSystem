package pl.dfs.distributedfilesystem.models;

public class FileOnFileList {
    public FileOnFileList() {

    }

    public FileOnFileList(String name, String size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    private String name;
    private String size;
}
