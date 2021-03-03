package de.craftedcrime.bungee.servermanager;

import de.craftedcrime.bungee.servermanager.commands.ServerCommand;
import de.craftedcrime.bungee.servermanager.database.MySQLHandler;
import de.craftedcrime.bungee.servermanager.handler.ServerHandler;
import de.craftedcrime.bungee.servermanager.models.ServerObject;
import de.craftedcrime.bungee.servermanager.utils.GeneralUtils;
import de.craftedcrime.bungee.servermanager.utils.IPValidationUtils;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.Level;

public final class Servermanager extends Plugin {

    // General Plugin to orchestrate new servers, without reloading the entire server !
    private HashMap<String, ServerObject> lobbyMap = new HashMap<>();
    private HashMap<String, ServerObject> noLobbiesMap = new HashMap<>();
    private ServerHandler serverHandler;
    private Configuration configuration;

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

    // -------------- UTILS -------------- //
    // ------ IP Validation Utils ------ //
    private IPValidationUtils ipValidationUtils;
    // ------ General Utils ------ //
    private GeneralUtils generalUtils;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // TODO: load config implementation
        ipValidationUtils = new IPValidationUtils();
        generalUtils = new GeneralUtils();
        loadConfig();
        this.serverHandler = new ServerHandler(this);
        this.mySQLHandler = new MySQLHandler(db_name, db_url, db_username, db_password, this);
        // TODO: load servers implementation


        // register all commands below here
        getProxy().getPluginManager().registerCommand(this, new ServerCommand(this));
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
        if (!configFile.exists()) {
            try {
                Files.copy(getResourceAsStream("config.yml"), configFile.toPath());
                configFile = new File(getDataFolder(), "config.yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            // ------------------------------ MYSQL LOAD CONFIG ------------------------------
            // ---------- Load HOSTNAME and PORT ----------
            if ((configuration.get("server.connections.mysql.hostname") != null) && (configuration.get("server.connections.mysql.port") != null)) {
                this.db_url = configuration.getString("server.connections.mysql.hostname") + ":" + configuration.getInt("server.connections.mysql.port");
            } else {
                getProxy().stop("§4ERROR! §cUnable to load MySQL config, because hostname and/or port is not given. Without that the ServerManager Plugin cant be used!");
            }

            // ---------- Load DATABASE ----------
            if (configuration.get("server.connections.mysql.database") != null) {
                this.db_name = configuration.getString("server.connections.mysql.database");
            } else {
                getProxy().stop("§4ERROR! §cUnable to load MySQL config, because the database is not given, it's necessary.");
            }

            // ---------- Load USERNAME ----------
            if (configuration.get("server.connections.mysql.username") != null) {
                this.db_username = configuration.getString("server.connections.mysql.username");
            } else {
                getProxy().stop("§4ERROR! §cUnable to load MySQL config, because the username is not given, it's necessary.");
            }

            // ---------- Load PASSWORD ----------
            if (configuration.get("server.connections.mysql.password") != null) {
                this.db_password = configuration.getString("server.connections.mysql.password");
            } else {
                getLogger().log(Level.FINER, "You haven set a mysql password. This can work, but it's highly recommended to use one!!");
                this.db_password = "";
            }


        } catch (IOException e) {
            e.printStackTrace();
            getProxy().stop("§4ERROR! §cUnable to load the configuration file! Please contact the developer with log details, if you don't know what to do!");
        }


        // loading the configuration

    }

    private void loadServers() {

    }

    public HashMap<String, ServerObject> getLobbyMap() {
        return lobbyMap;
    }

    public HashMap<String, ServerObject> getNoLobbiesMap() {
        return noLobbiesMap;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    public MySQLHandler getMySQLHandler() {
        return mySQLHandler;
    }

    public GeneralUtils getGeneralUtils() {
        return generalUtils;
    }
}
