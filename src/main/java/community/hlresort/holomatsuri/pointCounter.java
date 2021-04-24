package community.hlresort.holomatsuri;

import community.hlresort.holomatsuri.commands.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class pointCounter extends JavaPlugin {
    public static pointCounter plugin;
    public static FileConfiguration config;
    public static Connection conn = null;

    @Override
    public void onEnable() {
        plugin = this;
        config = this.getConfig();

        saveDefaultConfig();
        connectToDb();

        // Commands
        this.getCommand("setarea").setExecutor(new setArea());
        this.getCommand("getarea").setExecutor(new getArea());
        this.getCommand("addchest").setExecutor(new addChest());
        this.getCommand("removechest").setExecutor(new removeChest());
        this.getCommand("points").setExecutor(new getPoints());

        // Event handlers
        this.getServer().getPluginManager().registerEvents(new BlockEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new ChestEventHandler(), this);

        // Database initialization
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS points (uuid TEXT PRIMARY KEY, BuildPoints INTEGER NOT NULL, DeliveryPoints INTEGER NOT NULL) WITHOUT ROWID;");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS chests (x INTEGER NOT NULL, y INTEGER NOT NULL, z INTEGER NOT NULL);");
            statement.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        getLogger().info("Plugin loaded!");
    }

    @Override
    public void onDisable() {
        plugin = null;

        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        conn = null;
    }

    public void connectToDb() {
        try {
            this.getDataFolder().mkdirs();
            String url = "jdbc:sqlite:" + this.getDataFolder().getAbsolutePath() + "/data.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
