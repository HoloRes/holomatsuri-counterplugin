package community.hlresort.holomatsuri;

import community.hlresort.holomatsuri.commands.getArea;
import community.hlresort.holomatsuri.commands.setArea;
import community.hlresort.holomatsuri.BlockEventHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

        // Event handlers
        this.getServer().getPluginManager().registerEvents(new BlockEventHandler(), this);

        getLogger().info("Plugin loaded!");
    }

    @Override
    public void onDisable() {
        plugin = null;
        conn = null;
    }

    public void connectToDb() {
        try {
            this.getDataFolder().mkdirs();
            String url = "jdbc:sqlite:" + this.getDataFolder().getAbsolutePath() + "/data.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
