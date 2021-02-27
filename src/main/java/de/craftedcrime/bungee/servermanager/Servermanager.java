package de.craftedcrime.bungee.servermanager;

import de.craftedcrime.bungee.servermanager.database.MySQLHandler;
import de.craftedcrime.bungee.servermanager.handler.ServerHandler;
import de.craftedcrime.bungee.servermanager.models.ServerObject;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.HashMap;

public final class Servermanager extends Plugin {

    // General Plugin to orchestrate new servers, without reloading the entire server !

    private HashMap<String, ServerObject> serverMap = new HashMap<>();
    private ServerHandler serverHandler;

    // -------------- VARIABLES -------------- //
    // ------ DATABASE ------ //
    // Connection to DB is used for storing server data
    // without a DB connection this plugin can't be used
    private String db_name = "";
    private String db_url = "";
    private String db_username = "";
    private String db_password = "";

    // -------------- HANDLERS -------------- //
    // ------ MySQL Handler ------ //
    private MySQLHandler mySQLHandler;


    @Override
    public void onEnable() {
        // Plugin startup logic
        // TODO: load config implementation
        this.serverHandler = new ServerHandler(this);
        this.mySQLHandler = new MySQLHandler(db_name, db_url, db_username, db_password, this);
        // TODO: load servers implementation
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");
    }

    private void loadServers() {
        serverMap = mySQLHandler.loadAllServers();
    }

    public HashMap<String, ServerObject> getServerMap() {
        return serverMap;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    public MySQLHandler getMySQLHandler() {
        return mySQLHandler;
    }
}
