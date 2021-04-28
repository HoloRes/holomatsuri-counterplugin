package community.hlresort.holomatsuri;

import community.hlresort.holomatsuri.commands.*;
import community.hlresort.holomatsuri.handlers.BlockEventHandler;
import community.hlresort.holomatsuri.handlers.ChestEventHandler;
import org.bukkit.Material;
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
        this.getCommand("export").setExecutor(new export());

        // Event handlers
        this.getServer().getPluginManager().registerEvents(new BlockEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new ChestEventHandler(), this);

        // Database initialization
        try {
            Statement statement = conn.createStatement();
            //! Deprecated in favor of individual block counting
            //! statement.executeUpdate("CREATE TABLE IF NOT EXISTS points (uuid TEXT PRIMARY KEY, DeliveryPoints INTEGER NOT NULL) WITHOUT ROWID;");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS chests (x INTEGER NOT NULL, y INTEGER NOT NULL, z INTEGER NOT NULL, world TEXT NOT NULL);");
            StringBuilder blocksTable = new StringBuilder();
            for(int i = 0; i < Material.values().length; i++) {
                if(Material.values()[i].isBlock() && !Material.values()[i].isAir()) blocksTable.append(Material.values()[i].name()).append(" INTEGER DEFAULT 0, ");
            }
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS builds (uuid TEXT PRIMARY KEY," + blocksTable.substring(0, blocksTable.length() - 2) + ") WITHOUT ROWID;");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS delivery (uuid TEXT PRIMARY KEY," + blocksTable.substring(0, blocksTable.length() - 2) + ") WITHOUT ROWID;");
            statement.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        getLogger().info("Plugin loaded!");
    }

    @Override
    public void onDisable() {
        plugin = null;

        if (conn != null) {
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
