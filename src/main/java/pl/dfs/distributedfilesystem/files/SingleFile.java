package pl.dfs.distributedfilesystem.files;

public class SingleFile {
    private String name;
    private int size;
    private String node;
    private String path;

    public SingleFile(String name, int size, String path,String node) {
        this.name = name;
        this.size = size;
        this.node = node;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
