package de.craftedcrime.bungee.servermanager;

import de.craftedcrime.bungee.servermanager.commands.GoToCommand;
import de.craftedcrime.bungee.servermanager.commands.HubCommand;
import de.craftedcrime.bungee.servermanager.commands.ServerCommand;
import de.craftedcrime.bungee.servermanager.commands.ServerManagerCommand;
import de.craftedcrime.bungee.servermanager.database.MySQLHandler;
import de.craftedcrime.bungee.servermanager.handler.ServerHandler;
import de.craftedcrime.bungee.servermanager.handler.WebEditHandler;
import de.craftedcrime.bungee.servermanager.http.WebEditClient;
import de.craftedcrime.bungee.servermanager.listeners.PostLoginListener;
import de.craftedcrime.bungee.servermanager.listeners.ServerConnectListener;
import de.craftedcrime.bungee.servermanager.listeners.ServerKickListener;
import de.craftedcrime.bungee.servermanager.utils.GeneralUtils;
import de.craftedcrime.bungee.servermanager.utils.IPValidationUtils;
import de.craftedcrime.infrastructure.servermanager.middleware.ServerObject;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

public final class Servermanager extends Plugin {

    // General Plugin to orchestrate new servers, without reloading the entire server !
    private HashMap<String, ServerObject> lobbyMap = new HashMap<>();
    private HashMap<String, ServerObject> noLobbiesMap = new HashMap<>();
    private ServerHandler serverHandler;
    private Configuration configuration;
    private WebEditClient webEditClient;

    // -------------- VARIABLES -------------- //
    // ------ DATABASE ------ //
    // Connection to DB is used for storing server data
    // without a DB connection this plugin can't be used
    private String db_name = "";
    private String db_url = "";
    private String db_username = "";
    private String db_password = "";

    // ------ GENERAL ------ //
    private boolean disableDefaultBungeeCommands = true;
    private boolean forceHub = true;

    // -------------- HANDLERS -------------- //
    // ------ MySQL Handler ------ //
    private MySQLHandler mySQLHandler;
    // ------ WebEditor Handler ------ //
    private WebEditHandler webEditHandler;

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
        this.webEditClient = new WebEditClient();
        this.webEditHandler = new WebEditHandler(this);

        loadServers();
        disableDefaultCommands();

        if (disableDefaultBungeeCommands) {
            getProxy().getPluginManager().registerCommand(this, new ServerCommand("server", this));
        } else {
            getProxy().getPluginManager().registerCommand(this, new ServerCommand("servers", this));
        }
        // register all commands below here
        getProxy().getPluginManager().registerCommand(this, new ServerManagerCommand("sm", this));
        getProxy().getPluginManager().registerCommand(this, new GoToCommand("goto", this));
        getProxy().getPluginManager().registerCommand(this, new HubCommand(this));
        // TODO: add command send (non disabled sendplayer)
        // TODO: add command glist
        // TODO: add command tm / teamchat / teammessage
        // TODO: add command teamlist / tl


        // register all listeners below here
        getProxy().getPluginManager().registerListener(this, new PostLoginListener(this));
        getProxy().getPluginManager().registerListener(this, new ServerKickListener(this));
        getProxy().getPluginManager().registerListener(this, new ServerConnectListener(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void disableDefaultCommands() {
        if (this.disableDefaultBungeeCommands) {
            getLogger().log(Level.INFO, "Disabling the default bungee commands. The commands /server, /glist, /send are disabled.");
            Collection<Plugin> pluginsList = getProxy().getPluginManager().getPlugins();
            for (Plugin p : pluginsList) {
                switch (p.getFile().getName()) {
                    case "cmd_server.jar":
                        getProxy().getPluginManager().unregisterCommands(p);
                        getLogger().log(Level.INFO, "Unregistering command /server");
                        break;
                    case "cmd_send.jar":
                        getProxy().getPluginManager().unregisterCommands(p);
                        getLogger().log(Level.INFO, "Unregistering command /send");
                        break;
                    case "cmd_list.jar":
                        getProxy().getPluginManager().unregisterCommands(p);
                        getLogger().log(Level.INFO, "Unregistering command /glist");
                        break;
                    case "cmd_find.jar":
                        getProxy().getPluginManager().unregisterCommands(p);
                        getLogger().log(Level.INFO, "Unregistering command /find");
                        break;
                    default:
                        break;
                }
            }
        } else {
            getLogger().log(Level.INFO, "Attention! The default bungeecord commands are still enabled! If you don't configure the permissions otherwise, this could be a security risk. " +
                    "Keep that in mind! Following commands should be deactivated: ()");
        }
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

            // ---------- Load Disable default bungee commands ----------
            if (configuration.get("server.disable_default_bungeecord_commands") != null) {
                this.disableDefaultBungeeCommands = configuration.getBoolean("server.disable_default_bungeecord_commands");
            } else {
                getLogger().log(Level.FINER, "The server.disable_default_bungeecord_commands option wasn't found in the config. It's was set to the default value (enabled). The default bungee " +
                        "commands are disabled, use those provided by the ServerManager-plugin.");
                this.disableDefaultBungeeCommands = true;
            }


        } catch (IOException e) {
            e.printStackTrace();
            getProxy().stop("§4ERROR! §cUnable to load the configuration file! Please contact the developer with log details, if you don't know what to do!");
        }


    }

    private void loadServers() {
        this.lobbyMap = serverHandler.initAllLobbies();
        this.noLobbiesMap = serverHandler.initAllNonLobbies();
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

    public boolean isForceHub() {
        return forceHub;
    }

    public WebEditClient getWebEditClient() {
        return webEditClient;
    }

    public WebEditHandler getWebEditHandler() {
        return webEditHandler;
    }

    public IPValidationUtils getIpValidationUtils() {
        return ipValidationUtils;
    }
}
