package community.hlresort.holomatsuri.handlers;

import community.hlresort.holomatsuri.pointCounter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class BlockEventHandler implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!Objects.equals(pointCounter.config.get("coordinates.world"), block.getLocation().getWorld().getName()))
            return;

        // Check if anything is null
        if (pointCounter.config.get("coordinates.x.start") == null
                || pointCounter.config.get("coordinates.z.start") == null
                || pointCounter.config.get("coordinates.x.end") == null
                || pointCounter.config.get("coordinates.z.end") == null) return;

        // Get all the coordinates
        int xStart = (int) ((int) pointCounter.config.get("coordinates.x.end") > (int) pointCounter.config.get("coordinates.x.start")
                ? pointCounter.config.get("coordinates.x.start") : pointCounter.config.get("coordinates.x.end"));
        int zStart = (int) ((int) pointCounter.config.get("coordinates.z.end") > (int) pointCounter.config.get("coordinates.z.start")
                ? pointCounter.config.get("coordinates.z.start") : pointCounter.config.get("coordinates.z.end"));
        int xEnd = (int) ((int) pointCounter.config.get("coordinates.x.end") > (int) pointCounter.config.get("coordinates.x.start")
                ? pointCounter.config.get("coordinates.x.end") : pointCounter.config.get("coordinates.x.start"));
        int zEnd = (int) ((int) pointCounter.config.get("coordinates.z.end") > (int) pointCounter.config.get("coordinates.z.start")
                ? pointCounter.config.get("coordinates.z.end") : pointCounter.config.get("coordinates.z.start"));

        // Get block coordinates
        int blockX = block.getLocation().getBlockX();
        int blockZ = block.getLocation().getBlockZ();

        // Check if it is in the build area
        if (blockX >= xStart && blockX <= xEnd && blockZ >= zStart && blockZ <= zEnd) {
            try {
                Statement statement = pointCounter.conn.createStatement();
                ResultSet result = statement.executeQuery("SELECT EXISTS(SELECT 1 FROM builds WHERE uuid=\"" + player.getUniqueId() + "\");");
                while (result.next()) {
                    if (result.getBoolean(1)) {
                        Statement updateRow = pointCounter.conn.createStatement();
                        updateRow.executeUpdate("UPDATE builds SET " + block.getType().name() + "=" + block.getType().name() + " + 1" + " WHERE uuid=\"" + player.getUniqueId() + "\";");
                        updateRow.close();
                    } else {
                        Statement insertRow = pointCounter.conn.createStatement();
                        insertRow.executeUpdate("INSERT INTO builds(uuid) VALUES (\"" + player.getUniqueId() + "\");");
                        insertRow.executeUpdate("UPDATE builds SET " + block.getType().name() + "=" + block.getType().name() + " + 1" + " WHERE uuid=\"" + player.getUniqueId() + "\";");
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

        if (!Objects.equals(pointCounter.config.get("coordinates.world"), block.getLocation().getWorld().getName()))
            return;

        // Check if anything is null
        if (pointCounter.config.get("coordinates.x.start") == null
                || pointCounter.config.get("coordinates.z.start") == null
                || pointCounter.config.get("coordinates.x.end") == null
                || pointCounter.config.get("coordinates.z.end") == null) return;

        // Get all the coordinates
        int xStart = (int) ((int) pointCounter.config.get("coordinates.x.end") > (int) pointCounter.config.get("coordinates.x.start")
                ? pointCounter.config.get("coordinates.x.start") : pointCounter.config.get("coordinates.x.end"));
        int zStart = (int) ((int) pointCounter.config.get("coordinates.z.end") > (int) pointCounter.config.get("coordinates.z.start")
                ? pointCounter.config.get("coordinates.z.start") : pointCounter.config.get("coordinates.z.end"));
        int xEnd = (int) ((int) pointCounter.config.get("coordinates.x.end") > (int) pointCounter.config.get("coordinates.x.start")
                ? pointCounter.config.get("coordinates.x.end") : pointCounter.config.get("coordinates.x.start"));
        int zEnd = (int) ((int) pointCounter.config.get("coordinates.z.end") > (int) pointCounter.config.get("coordinates.z.start")
                ? pointCounter.config.get("coordinates.z.end") : pointCounter.config.get("coordinates.z.start"));
        // Get block coordinates
        int blockX = block.getLocation().getBlockX();
        int blockZ = block.getLocation().getBlockZ();

        // Check if it is in the build area
        if (blockX >= xStart && blockX <= xEnd && blockZ >= zStart && blockZ <= zEnd) {
            try {
                Statement statement = pointCounter.conn.createStatement();
                ResultSet result = statement.executeQuery("SELECT EXISTS(SELECT 1 FROM builds WHERE uuid=\"" + player.getUniqueId() + "\");");
                while (result.next()) {
                    if (result.getBoolean(1)) {
                        Statement updateRow = pointCounter.conn.createStatement();
                        updateRow.executeUpdate("UPDATE builds SET " +  block.getType().name() + "=" + block.getType().name() + " - 1" + " WHERE uuid=\"" + player.getUniqueId() + "\";");
                        updateRow.close();
                    } else {
                        Statement insertRow = pointCounter.conn.createStatement();
                        insertRow.executeUpdate("INSERT INTO builds(uuid) VALUES (\"" + player.getUniqueId() + "\");");
                        insertRow.executeUpdate("UPDATE builds SET " +  block.getType().name() + "=" + block.getType().name() + " - 1" + " WHERE uuid=\"" + player.getUniqueId() + "\";");
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
