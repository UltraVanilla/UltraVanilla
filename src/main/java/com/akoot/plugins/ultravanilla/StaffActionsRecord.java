package com.akoot.plugins.ultravanilla;

import com.akoot.plugins.ultravanilla.stuff.StringUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StaffActionsRecord {

    private final UltraVanilla plugin;

    public StaffActionsRecord(UltraVanilla instance) {
        this.plugin = instance;
    }

    public void log(StaffAction staffAction) {
        try {
            getConnection().prepareStatement(
                "INSERT INTO " + plugin.getConfig().getString("sql.table") + " (created, expires, type, description, sources, targets) VALUES (" +
                    StringUtil.getSqlDate(staffAction.getCreated()) + ", " +
                    StringUtil.getSqlDate(staffAction.getExpires()) + ", " +
                    "'" + staffAction.getType().toString() + "', " +
                    "'" + staffAction.getDescription().replace("'", "''") + "', " +
                    "'" + staffAction.getSource() + "', " +
                    "'" + staffAction.getTarget() + "'" +
                    ")"
            ).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://" + plugin.getConfig().getString("sql.host") + "/" + plugin.getConfig().getString("sql.database") + "?useSSL=false";
            String username = plugin.getConfig().getString("sql.username");
            String password = plugin.getConfig().getString("sql.password");
            Class.forName(driver);

            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
