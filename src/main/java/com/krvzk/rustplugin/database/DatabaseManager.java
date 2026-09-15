package com.krvzk.rustplugin.database;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.sql.*;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private Connection connection;
    private final String databasePath;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.databasePath = plugin.getDataFolder() + "/database.db";
    }

    public void initialize() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);

            createTables();
            plugin.getLogger().info("Database initialized successfully!");
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        String createStructuresTable = "CREATE TABLE IF NOT EXISTS structures (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "uuid TEXT NOT NULL," +
                "type TEXT NOT NULL," +
                "x INTEGER NOT NULL," +
                "y INTEGER NOT NULL," +
                "z INTEGER NOT NULL," +
                "world TEXT NOT NULL," +
                "rotation INTEGER NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createStructuresTable);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close database: " + e.getMessage());
        }
    }
}
