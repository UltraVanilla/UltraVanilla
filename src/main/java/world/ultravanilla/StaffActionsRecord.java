package world.ultravanilla;

import java.sql.*;

public class StaffActionsRecord {

    private final UltraVanilla plugin;

    public StaffActionsRecord(UltraVanilla instance) {
        this.plugin = instance;
    }

    public void log(StaffAction staffAction) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                "INSERT INTO " + plugin.getConfig().getString("sql.table") + " (created, expires, type, description, sources, targets) VALUES (?, ?, ?, ?, ?, ?)");

            preparedStatement.setObject(1, new Timestamp(staffAction.getCreated()));
            preparedStatement.setObject(2, new Timestamp(staffAction.getExpires()));
            preparedStatement.setString(3, staffAction.getType().toString());
            preparedStatement.setString(4, staffAction.getDescription());
            preparedStatement.setString(5, staffAction.getSource());
            preparedStatement.setString(6, staffAction.getTarget());

            preparedStatement.executeUpdate();
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
