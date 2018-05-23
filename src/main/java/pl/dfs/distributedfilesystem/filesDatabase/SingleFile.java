package pl.dfs.distributedfilesystem.filesDatabase;

public class SingleFile {
    private String name;
    private int size;
    private String node;

    public SingleFile(String name, int size, String node) {
        this.name = name;
        this.size = size;
        this.node = node;
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
}
