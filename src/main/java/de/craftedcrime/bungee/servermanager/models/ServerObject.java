package de.craftedcrime.bungee.servermanager.models;
/*
 * Created by ian on 25.02.21
 * Location: de.craftedcrime.bungee.servermanager.servermanager.models
 * Created for the project servermanager with the name ServerObject
 */

public class ServerObject {

    private int server_id;
    private String serverName;
    private String ipAddress;
    private int port;
    private String accessType;
    private boolean active;

    public ServerObject(int server_id, String serverName, String ipAddress, int port, String accessType) {
        this.server_id = server_id;
        this.serverName = serverName;
        this.ipAddress = ipAddress;
        this.port = port;
        this.accessType = accessType;
    }

    public ServerObject() {
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
