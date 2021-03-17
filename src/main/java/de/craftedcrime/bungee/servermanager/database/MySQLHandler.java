package de.craftedcrime.bungee.servermanager.database;
/*
 * Created by ian on 26.02.21
 * Location: de.craftedcrime.bungee.servermanager.database
 * Created for the project servermanager with the name MySQLHandler
 */

import de.craftedcrime.bungee.servermanager.Servermanager;
import de.craftedcrime.bungee.servermanager.models.ServerObject;

import java.sql.*;
import java.util.HashMap;
import java.util.logging.Level;

public class MySQLHandler {

    // VARIABLES - Used to connect to the database
    private String db_name = "";
    private String db_url = "";
    private String db_username = "";
    private String db_password = "";

    // Connection
    private Connection connection;
    // statement
    private Statement statement;

    // Main class
    private Servermanager servermanager;


    public MySQLHandler(String db_name, String db_url, String db_username, String db_password, Servermanager servermanager) {
        this.db_name = db_name;
        this.db_url = db_url;
        this.db_username = db_username;
        this.db_password = db_password;
        this.servermanager = servermanager;
        this.connection = connectToDatabase(this.db_name, this.db_url, this.db_username, this.db_password);
        loadStatement();
        initDatabase();
    }

    // establish connection to the database server
    private Connection connectToDatabase(String db_name, String db_url, String db_username, String db_password) {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + db_url + "/" + db_name + "?user=" + db_username + "&password=" + db_password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "[Connect To Database] Failed to connect to the database.");
            servermanager.getProxy().stop("[Connect To Database] A non valid database connection has been entered. Please revisit you settings.");
        }
        return null;
    }

    private void loadStatement() {
        try {
            this.statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "[Load Statement] Failed to create statement! (please check your db-config)");
        }
    }

    public void fixConnection() {
        if (this.connection == null) {
            connection = connectToDatabase(db_name, db_password, db_username, db_password);
        }
    }

    private void initDatabase() {
        try {

            connection.prepareCall("create table if not exists server_manager(server_id int auto_increment primary key, server_name varchar(64) not null, server_ip varchar(64) not null, " +
                    "server_port int not null, server_access_type varchar(64) default 'all' not null, server_active boolean default true not null, server_max_players int default 20 not null);").execute();

            connection.prepareCall("create table if not exists server_manager_lobby ( lobby_id int auto_increment primary key, lobby_name varchar(64) not null, lobby_ip varchar(64) not null, " +
                    "lobby_port int not null, lobby_access_type varchar(64) default 'all' not null, lobby_active boolean default true not null, lobby_max_players int default 20 not null);").execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "[Init Database] Failed to execute the init statement, to create the necessary tables. Please check your database configuration!");
        }
    }

    public HashMap<String, ServerObject> loadAllActiveLobbies() {
        HashMap<String, ServerObject> smret = new HashMap<>();
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM server_manager_lobby WHERE lobby_active=true"); // get all entries from the server_manager_lobbies table
            while (rs.next()) {
                ServerObject serverObject = new ServerObject();
                serverObject.setServer_id(rs.getInt("lobby_id"));
                serverObject.setServerName(rs.getString("lobby_name"));
                serverObject.setIpAddress(rs.getString("lobby_ip"));
                serverObject.setPort(rs.getInt("lobby_port"));
                serverObject.setAccessType(rs.getString("lobby_access_type"));
                serverObject.setMaxPlayers(rs.getInt("lobby_max_players"));
                smret.put(serverObject.getServerName(), serverObject);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "[Load All Active Lobbies] Failed to load all lobby servers. Please check your database config.");
        }
        servermanager.getLogger().log(Level.INFO, "[Load All Active Lobbies] Loading all active lobbies from the database. " + smret.values().size() + " entries!");
        return smret;
    }

    public HashMap<String, ServerObject> loadAllLobbies() {
        HashMap<String, ServerObject> smret = new HashMap<>();
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM server_manager_lobby"); // get all entries from the server_manager_lobbies table
            while (rs.next()) {
                ServerObject serverObject = new ServerObject();
                serverObject.setServer_id(rs.getInt("lobby_id"));
                serverObject.setServerName(rs.getString("lobby_name"));
                serverObject.setIpAddress(rs.getString("lobby_ip"));
                serverObject.setPort(rs.getInt("lobby_port"));
                serverObject.setAccessType(rs.getString("lobby_access_type"));
                serverObject.setActive(rs.getBoolean("lobby_active"));
                serverObject.setMaxPlayers(rs.getInt("lobby_max_players"));
                smret.put(serverObject.getServerName(), serverObject);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "[Load All Lobbies] Failed to load all lobby servers. Please check your database config.");
        }
        return smret;
    }

    public HashMap<String, ServerObject> loadAllActiveNonLobbies() {
        HashMap<String, ServerObject> smret = new HashMap<>();
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM server_manager WHERE server_active=true"); // get all entries from the server_manager table
            while (rs.next()) {
                ServerObject serverObject = new ServerObject();
                serverObject.setServer_id(rs.getInt("server_id"));
                serverObject.setServerName(rs.getString("server_name"));
                serverObject.setIpAddress(rs.getString("server_ip"));
                serverObject.setPort(rs.getInt("server_port"));
                serverObject.setAccessType(rs.getString("server_access_type"));
                serverObject.setMaxPlayers(rs.getInt("server_max_players"));
                smret.put(serverObject.getServerName(), serverObject);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "[Load Active Non Lobbies] Failed to load all non lobby servers. Please check your database config.");
        }
        servermanager.getLogger().log(Level.INFO, "[Load Active Non Lobbies] Loading all active non-lobbies from the database. " + smret.values().size() + " entries!");
        return smret;
    }

    public HashMap<String, ServerObject> loadAllNonLobbies() {
        HashMap<String, ServerObject> smret = new HashMap<>();
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM server_manager"); // get all entries from the server_manager table
            while (rs.next()) {
                ServerObject serverObject = new ServerObject();
                serverObject.setServer_id(rs.getInt("server_id"));
                serverObject.setServerName(rs.getString("server_name"));
                serverObject.setIpAddress(rs.getString("server_ip"));
                serverObject.setPort(rs.getInt("server_port"));
                serverObject.setAccessType(rs.getString("server_access_type"));
                serverObject.setActive(rs.getBoolean("server_active"));
                serverObject.setMaxPlayers(rs.getInt("server_max_players"));
                smret.put(serverObject.getServerName(), serverObject);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "[Load All Non Lobbies] Failed to load all non lobby servers. Please check your database config.");
        }
        return smret;
    }

    public boolean serverExists(String servername) {
        boolean pre = false;
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM server_manager WHERE server_name='" + servername+"'");
            pre = rs.next();
            rs.close();
            ResultSet rsl = statement.executeQuery("SELECT * FROM server_manager_lobby WHERE lobby_name='" + servername+"'");
            pre = pre || rsl.next();
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "[Server Exists] Failed to check if a server exists. Check your database config.");
        }
        return pre;
    }

    public ServerObject getServerObjectByName(String servername) {
        ServerObject serverObject = null;
        try {
            ResultSet rs;
            if (isLobby(servername)) {
                rs = statement.executeQuery("SELECT * FROM server_manager_lobby WHERE lobby_name='" + servername + "'");
                if (rs.next()) {
                    serverObject = new ServerObject();
                    serverObject.setServerName(rs.getString("lobby_name"));
                    serverObject.setServer_id(rs.getInt("lobby_id"));
                    serverObject.setActive(rs.getBoolean("lobby_active"));
                    serverObject.setPort(rs.getInt("lobby_port"));
                    serverObject.setIpAddress(rs.getString("lobby_ip"));
                    serverObject.setAccessType(rs.getString("lobby_access_type"));
                    serverObject.setMaxPlayers(rs.getInt("lobby_max_players"));
                }
            } else {
                rs = statement.executeQuery("SELECT * FROM server_manager WHERE server_name='" + servername + "'");
                if (rs.next()) {
                    serverObject = new ServerObject();
                    serverObject.setServerName(rs.getString("server_name"));
                    serverObject.setServer_id(rs.getInt("server_id"));
                    serverObject.setActive(rs.getBoolean("server_active"));
                    serverObject.setPort(rs.getInt("server_port"));
                    serverObject.setIpAddress(rs.getString("server_ip"));
                    serverObject.setAccessType(rs.getString("server_access_type"));
                    serverObject.setMaxPlayers(rs.getInt("server_max_players"));
                }
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return serverObject;
    }

    // adds a minecraft server to the context
    public void addServer(ServerObject serverObject, boolean lobby) {
        if (!serverExists(serverObject.getServerName())) {
            try {
                PreparedStatement preparedStatement;
                if (!lobby) {
                    preparedStatement = connection.prepareStatement("INSERT INTO server_manager (server_name, server_ip, server_port, server_access_type) VALUES (?,?,?,?)");
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO server_manager_lobby (lobby_name, lobby_ip, lobby_port, lobby_access_type) VALUES (?,?,?,?)");
                }
                preparedStatement.setString(1, serverObject.getServerName());
                preparedStatement.setString(2, serverObject.getIpAddress());
                preparedStatement.setInt(3, serverObject.getPort());
                preparedStatement.setString(4, serverObject.getAccessType());
                preparedStatement.execute();
                servermanager.getLogger().log(Level.INFO, "[Add Server] Successfully saved '" + serverObject.getServerName() + "' to the database.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                servermanager.getLogger().log(Level.WARNING, "[Add Server] Failed to save the new server '" + serverObject.getServerName() + "' into the database. Please check your db-config!");
            }
        }
    }

    // deletes a minecraft server from the context
    public boolean deleteServer(String servername, boolean lobby) {
        if (serverExists(servername)) {
            try {
                PreparedStatement preparedStatement;
                if (!lobby) {
                    preparedStatement = connection.prepareStatement("DELETE FROM server_manager WHERE server_name = ?;");
                } else {
                    preparedStatement = connection.prepareStatement("DELETE FROM server_manager_lobby WHERE lobby_name = ?;");
                }
                preparedStatement.setString(1, servername);
                preparedStatement.execute();
                servermanager.getLogger().log(Level.INFO, "[Delete Server] Successfully deleted '" + servername + "' from the database.");
                servermanager.getNoLobbiesMap().remove(servername);
                servermanager.getLogger().log(Level.INFO, "[Delete Server] Successfully deleted '" + servername + "' from internal context.");
                return true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                servermanager.getLogger().log(Level.WARNING, "[Delete Server] Failed to delete the server '" + servername + "' from the database. Please check your db-config.");
                return false;
            }
        }
        return false;
    }

    public boolean deactivateServer(String servername, boolean lobby) {
        if (serverExists(servername)) {
            PreparedStatement preparedStatement;
            try {
                if (lobby) {
                    preparedStatement = connection.prepareStatement("UPDATE server_manager_lobby SET lobby_active = ? WHERE lobby_name = ?");
                } else {
                    preparedStatement = connection.prepareStatement("UPDATE server_manager SET server_active = ? WHERE server_name = ?");
                }
                preparedStatement.setBoolean(1, false);
                preparedStatement.setString(2, servername);
                preparedStatement.execute();
                return true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                servermanager.getLogger().log(Level.WARNING, "[Deactivate Server] Failed to deactivate the server '" + servername + "'. Please check you database config.");
                return false;
            }
        }
        return false;
    }

    public boolean activateServer(String servername, boolean lobby) {
        if (serverExists(servername)) {
            PreparedStatement preparedStatement;
            try {
                if (lobby) {
                    preparedStatement = connection.prepareStatement("UPDATE server_manager_lobby SET lobby_active = ? WHERE lobby_name = ?");
                } else {
                    preparedStatement = connection.prepareStatement("UPDATE server_manager SET server_active = ? WHERE server_name = ?");
                }
                preparedStatement.setBoolean(1, true);
                preparedStatement.setString(2, servername);
                preparedStatement.execute();
                return true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                servermanager.getLogger().log(Level.WARNING, "[Activate Server] Failed to activate the server '" + servername + "'. Please check you database config.");
                return false;
            }
        }
        return false;
    }

    public boolean isLobby(String servername) {
        boolean ret = false;

        try {
            ResultSet rs = statement.executeQuery("SELECT  * FROM server_manager_lobby WHERE lobby_name='" + servername + "'");
            ret = rs.next();
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }

    public boolean changePlayerCount(String servername, int playerCount) {
        boolean ret = false;
        if (serverExists(servername)) {
            PreparedStatement preparedStatement;
                try {
                    if (isLobby(servername)) {
                        preparedStatement = connection.prepareStatement("UPDATE server_manager_lobby SET lobby_max_players=? WHERE lobby_name=?");
                    } else {
                        preparedStatement = connection.prepareStatement("UPDATE server_manager SET server_max_players=? WHERE server_name=?");
                    }
                    preparedStatement.setInt(1, playerCount);
                    preparedStatement.setString(2, servername);
                    preparedStatement.execute();
                    ret = true;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
        } else {
            servermanager.getLogger().log(Level.WARNING, "[Max-Player amount change] The server '" + servername + "' doesn't exist, please check for correct spelling of the server name!");
        }
        return ret;
    }


}
