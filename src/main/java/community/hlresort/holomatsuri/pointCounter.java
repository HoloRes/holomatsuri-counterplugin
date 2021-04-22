package community.hlresort.holomatsuri;

import community.hlresort.holomatsuri.commands.setArea;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class pointCounter extends JavaPlugin implements Listener {
    public static pointCounter plugin;
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        plugin = this;
        config = this.getConfig();

        saveDefaultConfig();
        connectToDb();

        this.getCommand("setarea").setExecutor(new setArea());

        getLogger().info("Plugin loaded!");
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

    public void connectToDb() {
        Connection conn = null;
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
