package community.hlresort.holomatsuri.commands;

import community.hlresort.holomatsuri.pointCounter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class getPoints implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player!");
            return false;
        }

        try {
            int buildPoints = 0;
            int deliveryPoints = 0;

            Statement buildExistsStatement = pointCounter.conn.createStatement();
            ResultSet buildExistsResult = buildExistsStatement.executeQuery("SELECT EXISTS(SELECT 1 FROM builds WHERE uuid=\"" + player.getUniqueId() + "\");");

            Statement deliveryExistsStatement = pointCounter.conn.createStatement();
            ResultSet deliveryExistsResult = deliveryExistsStatement.executeQuery("SELECT EXISTS(SELECT 1 FROM delivery WHERE uuid=\"" + player.getUniqueId() + "\");");

            while (buildExistsResult.next()) {
                if (buildExistsResult.getBoolean(1)) {
                    Statement getUser = pointCounter.conn.createStatement();
                    ResultSet user = getUser.executeQuery("SELECT * FROM builds WHERE uuid=\"" + player.getUniqueId() + "\";");
                    while (user.next()) {
                        for(int i = 1; i < user.getMetaData().getColumnCount(); i++) {
                            if(pointCounter.config.get("blocks.build." + user.getMetaData().getColumnName(i)) != null) buildPoints = buildPoints + ((int) pointCounter.config.get("blocks.build." + user.getMetaData().getColumnName(i)) * user.getInt(i));
                        }
                    }
                    getUser.close();
                }
            }
            buildExistsStatement.close();

            while (deliveryExistsResult.next()) {
                if (deliveryExistsResult.getBoolean(1)) {
                    Statement getUser = pointCounter.conn.createStatement();
                    ResultSet user = getUser.executeQuery("SELECT * FROM delivery WHERE uuid=\"" + player.getUniqueId() + "\";");
                    while (user.next()) {
                        for(int i = 1; i < user.getMetaData().getColumnCount(); i++) {
                            if(pointCounter.config.get("blocks.chest." + user.getMetaData().getColumnName(i)) != null) deliveryPoints = deliveryPoints + ((int) pointCounter.config.get("blocks.chest." + user.getMetaData().getColumnName(i)) * user.getInt(i));
                        }
                    }
                    getUser.close();
                }
            }
            deliveryExistsStatement.close();

            player.sendMessage("Build points: " + ChatColor.AQUA + buildPoints + ChatColor.WHITE + "\nDelivery points: " + ChatColor.AQUA + deliveryPoints);
        } catch (SQLException e) {
            player.sendMessage("Something went wrong, please try again.");
            System.out.println(e.getMessage());
        }
        return true;
    }
}
