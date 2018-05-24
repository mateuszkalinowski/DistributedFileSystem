package pl.dfs.distributedfilesystem.models;

public class ObjectOnTheList {
    private String name;
    private String size;
    private String replication;
    private String type;

    public ObjectOnTheList() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReplication() {
        return replication;
    }

    public void setReplication(String replication) {
        this.replication = replication;
    }

    public ObjectOnTheList(String name, String size, String replication, String type) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.replication = replication;
    }
}
