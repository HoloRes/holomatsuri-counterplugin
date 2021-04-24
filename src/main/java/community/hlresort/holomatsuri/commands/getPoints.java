package community.hlresort.holomatsuri.commands;

import community.hlresort.holomatsuri.pointCounter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class getPoints implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player!");
            return false;
        }

        Player player = ((Player) sender).getPlayer();
        if(player == null) return false;

        try {
            Statement statement = pointCounter.conn.createStatement();
            ResultSet result = statement.executeQuery("SELECT EXISTS(SELECT 1 FROM points WHERE uuid=\"" + player.getUniqueId() + "\");");
            while(result.next()) {
                if(result.getBoolean(1)) {
                    Statement getUser = pointCounter.conn.createStatement();
                    ResultSet user = getUser.executeQuery("SELECT BuildPoints, DeliveryPoints FROM points WHERE uuid=\"" + player.getUniqueId() + "\";");
                    while(user.next()) {
                        player.sendMessage("Build points: " + ChatColor.AQUA + user.getInt("BuildPoints") + ChatColor.WHITE + "\nDelivery points: " + ChatColor.AQUA + user.getInt("DeliveryPoints"));
                    }
                    getUser.close();
                } else {
                    player.sendMessage("Build points: " + ChatColor.AQUA + "0" + ChatColor.WHITE + "\nDelivery points: " + ChatColor.AQUA + "0");
                }
            }
            statement.close();
        } catch (SQLException e) {
            player.sendMessage("Something went wrong, please try again.");
            System.out.println(e.getMessage());
        }
        return true;
    }
}
