package pl.dfs.distributedfilesystem.models;

public class ToChange {
    private String address;
    private String command;
    private String from;
    private String to;

    public ToChange(String address, String command, String from, String to) {
        this.address = address;
        this.command = command;
        this.from = from;
        this.to = to;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
