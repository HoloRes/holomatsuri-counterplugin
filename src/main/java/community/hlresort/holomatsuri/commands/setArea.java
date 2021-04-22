package community.hlresort.holomatsuri.commands;

import community.hlresort.holomatsuri.pointCounter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class setArea implements CommandExecutor {
    Integer xStart;
    Integer zStart;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player!");
            return false;
        }

        Player player = (Player) sender;

        if(xStart != null && zStart != null) {
            pointCounter.config.set("coordinates.x.start", xStart);
            pointCounter.config.set("coordinates.z.start", zStart);
            pointCounter.config.set("coordinates.x.end", player.getLocation().getBlockX());
            pointCounter.config.set("coordinates.z.end", player.getLocation().getBlockZ());
            pointCounter.plugin.saveConfig();

            sender.sendMessage("Start location: " + ChatColor.AQUA + xStart + ", " + zStart + ChatColor.WHITE + "\n" + "End location: " + ChatColor.AQUA + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ());

            xStart = null;
            zStart = null;
        } else {
            xStart = player.getLocation().getBlockX();
            zStart = player.getLocation().getBlockZ();

            sender.sendMessage("Start location: " + ChatColor.AQUA + xStart + ", " + zStart);
        }

        return true;
    }
}
