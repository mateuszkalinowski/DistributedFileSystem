package pl.dfs.distributedfilesystem.models;

public class ObjectOnTheList {
    private String name;
    private String size;
    private String replication;
    private String type;
    private String icon;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ObjectOnTheList(String name, String size, String replication, String type, String icon) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.replication = replication;
        this.icon = icon;
    }
}
