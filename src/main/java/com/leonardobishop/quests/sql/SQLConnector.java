package com.leonardobishop.quests.sql;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.enums.StoreType;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLConnector {
    private final Quests plugin;
    private Connection connection = null;
    private String tablePrefix;
    private StoreType storeType;

    public SQLConnector(Quests plugin) {
        this.plugin = plugin;
    }

    public void setStoreType(StoreType storeType) {
        this.storeType = storeType;
    }

    public StoreType getStoreType() {
        return this.storeType;
    }

    public void loadMySQL(String host, String user, String password, String port, String name, boolean useSSL, String tablePrefix) {
        this.tablePrefix = tablePrefix;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + name + "?user=" + user + "&password=" + password + "&useSSL=" + useSSL + "&autoReconnect=true");
            this.createTables();
        } catch (SQLException exception) {
            exception.printStackTrace();
            // Oh no
        }
    }

    public void loadSQL(String fileName) {
        try {
            File file = new File(Quests.get().getDataFolder() + File.separator + fileName); // no need to create the file or check is exists or not
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            this.createTables();
        } catch (SQLException exception) {
            exception.printStackTrace();
            // Oh no
        }
    }

    private void createTables() throws SQLException {
        // quests IDs can only go 50 characters long (the limitation of varchar(50))
        PreparedStatement ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS progress(id INT PRIMARY KEY AUTO_INCREMENT , player_uuid varchar(36) NOT NULL , quest_id varchar(50) , started bool , completed bool , completed_before bool , completition_date BIGINT(19));");
        // create the table "progress"
        ps.executeUpdate();
        if (ps != null) //Close the prepared statement
            ps.close();
        ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS task_progress(id INT PRIMARY KEY AUTO_INCREMENT , player_uuid varchar(36) NOT NULL , quest_id varchar(50) , task_id varchar(50) , completed bool, progress varchar(64));");
        // create the table "task-progress"
        ps.executeUpdate();
        if (ps != null) //Close the prepared statement
            ps.close();
    }

    public String getTablePrefix() {
        return this.tablePrefix;
    }

    /**
     * @return The connection to the sql database
     * <p>
     * If the connection is not used, return null
     */
    public Connection getConnection() {
        return this.connection;
    }

    public void stopConnection() {
        if (connection == null)
            return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
