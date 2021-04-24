package community.hlresort.holomatsuri;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BlockEventHandler implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Check if anything is null
        if(pointCounter.config.get("coordinates.x.start") == null
                || pointCounter.config.get("coordinates.z.start") == null
                || pointCounter.config.get("coordinates.x.end") == null
                || pointCounter.config.get("coordinates.z.end") == null) return;

        // Get all the coordinates
        Integer xStart = (Integer) ((Integer) pointCounter.config.get("coordinates.x.end") > (Integer) pointCounter.config.get("coordinates.x.start")
                ? pointCounter.config.get("coordinates.x.start") : pointCounter.config.get("coordinates.x.end"));
        Integer zStart = (Integer) ((Integer) pointCounter.config.get("coordinates.z.end") > (Integer) pointCounter.config.get("coordinates.z.start")
                ? pointCounter.config.get("coordinates.z.start") : pointCounter.config.get("coordinates.z.end"));
        Integer xEnd = (Integer) ((Integer) pointCounter.config.get("coordinates.x.end") > (Integer) pointCounter.config.get("coordinates.x.start")
                ? pointCounter.config.get("coordinates.x.end") : pointCounter.config.get("coordinates.x.start"));
        Integer zEnd = (Integer) ((Integer) pointCounter.config.get("coordinates.z.end") > (Integer) pointCounter.config.get("coordinates.z.start")
                ? pointCounter.config.get("coordinates.z.end") : pointCounter.config.get("coordinates.z.start"));

        // Get block coordinates
        Integer blockX = block.getLocation().getBlockX();
        Integer blockZ = block.getLocation().getBlockZ();

        // Check if it is in the build area
        if(blockX >= xStart && blockX <= xEnd && blockZ >= zStart && blockZ <= zEnd) {
            try {
                Statement statement = pointCounter.conn.createStatement();
                ResultSet result = statement.executeQuery("SELECT EXISTS(SELECT 1 FROM points WHERE uuid=\"" + player.getUniqueId() + "\");");
                while(result.next()) {
                    if(result.getBoolean(1)) {
                        Statement updateRow = pointCounter.conn.createStatement();
                        updateRow.executeUpdate("UPDATE points SET BuildPoints = BuildPoints + " + pointCounter.config.get("blocks." + block.getType().name()) + " WHERE uuid=\"" + player.getUniqueId() + "\";");
                        updateRow.close();
                    } else {
                        Statement insertRow = pointCounter.conn.createStatement();
                        insertRow.executeUpdate("INSERT INTO points(uuid, BuildPoints, DeliveryPoints) VALUES (\"" + player.getUniqueId() + "\", " + pointCounter.config.get("blocks." + block.getType().name()) + ", 0);");
                        insertRow.close();
                    }
                }
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Check if anything is null
        if(pointCounter.config.get("coordinates.x.start") == null
                || pointCounter.config.get("coordinates.z.start") == null
                || pointCounter.config.get("coordinates.x.end") == null
                || pointCounter.config.get("coordinates.z.end") == null) return;

        // Get all the coordinates
        Integer xStart = (Integer) ((Integer) pointCounter.config.get("coordinates.x.end") > (Integer) pointCounter.config.get("coordinates.x.start")
                        ? pointCounter.config.get("coordinates.x.start") : pointCounter.config.get("coordinates.x.end"));
        Integer zStart = (Integer) ((Integer) pointCounter.config.get("coordinates.z.end") > (Integer) pointCounter.config.get("coordinates.z.start")
                ? pointCounter.config.get("coordinates.z.start") : pointCounter.config.get("coordinates.z.end"));
        Integer xEnd = (Integer) ((Integer) pointCounter.config.get("coordinates.x.end") > (Integer) pointCounter.config.get("coordinates.x.start")
                ? pointCounter.config.get("coordinates.x.end") : pointCounter.config.get("coordinates.x.start"));
        Integer zEnd = (Integer) ((Integer) pointCounter.config.get("coordinates.z.end") > (Integer) pointCounter.config.get("coordinates.z.start")
                ? pointCounter.config.get("coordinates.z.end") : pointCounter.config.get("coordinates.z.start"));

        // Get block coordinates
        Integer blockX = block.getLocation().getBlockX();
        Integer blockZ = block.getLocation().getBlockZ();

        // Check if it is in the build area
        if(blockX >= xStart && blockX <= xEnd && blockZ >= zStart && blockZ <= zEnd) {
            try {
                Statement statement = pointCounter.conn.createStatement();
                ResultSet result = statement.executeQuery("SELECT EXISTS(SELECT 1 FROM points WHERE uuid=\"" + player.getUniqueId() + "\");");
                while(result.next()) {
                    if(result.getBoolean(1)) {
                        Statement updateRow = pointCounter.conn.createStatement();
                        updateRow.executeUpdate("UPDATE points SET BuildPoints = BuildPoints - " + pointCounter.config.get("blocks." + block.getType().name()) + " WHERE uuid=\"" + player.getUniqueId() + "\";");
                        updateRow.close();
                    } else {
                        Statement insertRow = pointCounter.conn.createStatement();
                        insertRow.executeUpdate("INSERT INTO points(uuid, BuildPoints, DeliveryPoints) VALUES (\"" + player.getUniqueId() + "\", -" + pointCounter.config.get("blocks." + block.getType().name()) + ", 0);");
                        insertRow.close();
                    }
                }
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
