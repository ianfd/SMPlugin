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
    }

    // establish connection to the database server
    private Connection connectToDatabase(String db_name, String db_url, String db_username, String db_password) {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + db_url + "/" + db_name + "?user=" + db_username + "&password=" + db_password);
        } catch (SQLException throwables) {
            servermanager.getLogger().log(Level.SEVERE, "Failed to connect to the database.");
            servermanager.getProxy().stop("A non valid database connection has been entered. Please revisit you settings.");
            throwables.printStackTrace();
        }
        return null;
    }

    private void loadStatement() {
        try {
            this.statement = connection.createStatement();
        } catch (SQLException throwables) {
            servermanager.getLogger().log(Level.SEVERE, "Failed to create statement! (please check your db-config)");
            throwables.printStackTrace();
        }
    }

    public void fixConnection() {
        if (this.connection == null) {
            connection = connectToDatabase(db_name, db_password, db_username, db_password);
        }
    }

    public void initDatabase() {
        try {
            // create table for further usage
            connection.prepareCall("create table if not exists server_manager" +
                    "(\n" +
                    "    server_id            int auto_increment primary key," +
                    "    server_name          varchar(64)          not null," +
                    "    server_ip            varchar(64)          not null," +
                    "    server_port          int                  not null," +
                    "    server_is_restricted tinyint(1) default 0 not null)").execute();

        } catch (SQLException throwables) {
            servermanager.getLogger().log(Level.SEVERE, "Failed to execute the init statement, to create the necessary tables. Please check your database configuration!");
            throwables.printStackTrace();
        }
    }

    public HashMap<String, ServerObject> loadAllServers() {
        HashMap<String, ServerObject> smret = new HashMap<>();
        try {
            ResultSet rs = statement.executeQuery("SELECT  * FROM server_manager"); // get all entries from the server_manager table
            while (rs.next()) {
                ServerObject serverObject = new ServerObject();
                serverObject.setServer_id(rs.getInt("server_id"));
                serverObject.setServerName(rs.getString("server_name"));
                serverObject.setIpAddress(rs.getString("server_ip"));
                serverObject.setPort(rs.getInt("server_port"));
                serverObject.setRestrictedAccess(rs.getBoolean("server_is_restricted"));
                smret.put(serverObject.getServerName(), serverObject);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return smret;
    }

    public boolean serverExists(String servername) {
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM server_manager WHERE server_name = " + servername + "");
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void addServer(ServerObject serverObject) {
        if (!serverExists(serverObject.getServerName())) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO server_manager (server_name, server_ip, server_port, server_is_restricted) VALUES (?,?,?,?)");
                preparedStatement.setString(1, serverObject.getServerName());
                preparedStatement.setString(2, serverObject.getIpAddress());
                preparedStatement.setInt(3, serverObject.getPort());
                preparedStatement.setBoolean(4, serverObject.isRestrictedAccess());
                preparedStatement.execute();
            } catch (SQLException throwables) {

                throwables.printStackTrace();
            }
        }
    }


}
