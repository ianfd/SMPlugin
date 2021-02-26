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
    private boolean restrictedAccess = false;

    public ServerObject(String serverName, String ipAddress, int port, boolean restrictedAccess) {
        this.serverName = serverName;
        this.ipAddress = ipAddress;
        this.port = port;
        this.restrictedAccess = restrictedAccess;
    }

    public ServerObject(String serverName, String ipAddress, int port) {
        this.serverName = serverName;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public ServerObject() {
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

    public boolean isRestrictedAccess() {
        return restrictedAccess;
    }

    public void setRestrictedAccess(boolean restrictedAccess) {
        this.restrictedAccess = restrictedAccess;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }
}
