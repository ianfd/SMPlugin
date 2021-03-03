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
        servermanager.getLogger().log(Level.INFO, "MySQL settings: " + db_url + "," + db_name + "," + db_username + "," + db_password);
        try {
            return DriverManager.getConnection("jdbc:mysql://" + db_url + "/" + db_name + "?user=" + db_username + "&password=" + db_password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "Failed to connect to the database.");
            servermanager.getProxy().stop("A non valid database connection has been entered. Please revisit you settings.");
        }
        return null;
    }

    private void loadStatement() {
        try {
            this.statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "Failed to create statement! (please check your db-config)");
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
                    "server_port int not null, server_access_type varchar(64) default 'ALL' not null, server_active boolean default true not null);").execute();

            connection.prepareCall("create table if not exists server_manager_lobby ( lobby_id int auto_increment primary key, lobby_name varchar(64) not null, lobby_ip varchar(64) not null, " +
                    "lobby_port int not null, lobby_access_type varchar(64) default 'ALL' not null, lobby_active boolean default true not null);").execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "Failed to execute the init statement, to create the necessary tables. Please check your database configuration!");
        }
    }

    public HashMap<String, ServerObject> loadAllActiveLobbies() {
        HashMap<String, ServerObject> smret = new HashMap<>();
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM server_manager_lobby WHERE lobby_active = true"); // get all entries from the server_manager_lobbies table
            while (rs.next()) {
                ServerObject serverObject = new ServerObject();
                serverObject.setServer_id(rs.getInt("lobby_id"));
                serverObject.setServerName(rs.getString("lobby_name"));
                serverObject.setIpAddress(rs.getString("lobby_ip"));
                serverObject.setPort(rs.getInt("lobby_port"));
                serverObject.setAccessType(rs.getString("lobby_access_type"));
                smret.put(serverObject.getServerName(), serverObject);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "Failed to load all lobby servers. Please check your database config.");
        }
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
                smret.put(serverObject.getServerName(), serverObject);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "Failed to load all lobby servers. Please check your database config.");
        }
        return smret;
    }

    public HashMap<String, ServerObject> loadAllNonActiveLobbies() {
        HashMap<String, ServerObject> smret = new HashMap<>();
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM server_manager WHERE server_active = true"); // get all entries from the server_manager table
            while (rs.next()) {
                ServerObject serverObject = new ServerObject();
                serverObject.setServer_id(rs.getInt("server_id"));
                serverObject.setServerName(rs.getString("server_name"));
                serverObject.setIpAddress(rs.getString("server_ip"));
                serverObject.setPort(rs.getInt("server_port"));
                serverObject.setAccessType(rs.getString("server_access_type"));
                smret.put(serverObject.getServerName(), serverObject);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "Failed to load all non lobby servers. Please check your database config.");
        }
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
                smret.put(serverObject.getServerName(), serverObject);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "Failed to load all non lobby servers. Please check your database config.");
        }
        return smret;
    }

    public boolean serverExists(String servername) {
        boolean pre = false;
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM server_manager WHERE server_name = '" + servername + "'");
            pre = rs.next();
            rs.close();
            ResultSet rsl = statement.executeQuery("SELECT * FROM server_manager_lobby WHERE lobby_name = '" + servername + "'");
            pre = pre || rsl.next();
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            servermanager.getLogger().log(Level.WARNING, "Failed to check if a server exists. Check your database config.");
        }
        return pre;
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
                servermanager.getLogger().log(Level.INFO, "Successfully saved '" + serverObject.getServerName() + "' to the database.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                servermanager.getLogger().log(Level.WARNING, "Failed to save the new server '" + serverObject.getServerName() + "' into the database. Please check your db-config!");
            }
        }
    }

    // deletes a minecraft server from the context
    public boolean deleteServer(String servername, boolean lobby) {
        if (serverExists(servername)) {
            try {
                PreparedStatement preparedStatement;
                if (!lobby) {
                    preparedStatement = connection.prepareStatement("DELETE FROM server_manager WHERE server_name = ?");
                } else {
                    preparedStatement = connection.prepareStatement("DELETE FROM server_manager_lobby WHERE lobby_name = ?");
                }
                preparedStatement.setString(1, servername);
                preparedStatement.execute();
                servermanager.getLogger().log(Level.INFO, "Successfully deleted '" + servername + "' from the database.");
                servermanager.getNoLobbiesMap().remove(servername);
                servermanager.getLogger().log(Level.INFO, "Successfully deleted '" + servername + "' from internal context.");
                return true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                servermanager.getLogger().log(Level.WARNING, "Failed to delete the server '" + servername + "' from the database. Please check your db-config.");
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
                servermanager.getLogger().log(Level.WARNING, "Failed to deactivate the server '" + servername + "'. Please check you database config.");
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
                servermanager.getLogger().log(Level.WARNING, "Failed to deactivate the server '" + servername + "'. Please check you database config.");
                return false;
            }
        }
        return false;
    }


}
