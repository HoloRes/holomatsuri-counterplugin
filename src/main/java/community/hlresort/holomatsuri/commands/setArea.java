package community.hlresort.holomatsuri.commands;

import community.hlresort.holomatsuri.pointCounter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class setArea implements CommandExecutor {
    Integer xStart;
    Integer zStart;
    String world;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player!");
            return false;
        }

        if (args.length > 0 && args[0] != null && args[0].toLowerCase(Locale.ROOT).equals("cancel")) {
            xStart = null;
            zStart = null;
            world = null;
            player.sendMessage("Cancelled.");
            return true;
        }

        if (xStart != null && zStart != null) {
            if (!player.getLocation().getWorld().getName().equals(world)) {
                sender.sendMessage("The area must be in the same world! Run the command with cancel as argument to cancel.");
                return true;
            }
            pointCounter.config.set("coordinates.x.start", xStart);
            pointCounter.config.set("coordinates.z.start", zStart);
            pointCounter.config.set("coordinates.x.end", player.getLocation().getBlockX());
            pointCounter.config.set("coordinates.z.end", player.getLocation().getBlockZ());
            pointCounter.config.set("coordinates.world", world);
            pointCounter.plugin.saveConfig();

            sender.sendMessage("Start location: " + ChatColor.AQUA + xStart + ", " + zStart + ChatColor.WHITE + "\n" + "End location: " + ChatColor.AQUA + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ());

            xStart = null;
            zStart = null;
        } else {
            xStart = player.getLocation().getBlockX();
            zStart = player.getLocation().getBlockZ();
            world = player.getLocation().getWorld().getName();

            sender.sendMessage("Start location: " + ChatColor.AQUA + xStart + ", " + zStart + ChatColor.WHITE + "\nRun this command again on the other corner of the area.");
        }

        return true;
    }
}
