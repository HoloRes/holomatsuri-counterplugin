package community.hlresort.holomatsuri.commands;

import community.hlresort.holomatsuri.pointCounter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class getArea implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        sender.sendMessage("Start location: " + ChatColor.AQUA + pointCounter.config.get("coordinates.x.start") + ", " + pointCounter.config.get("coordinates.z.start") + ChatColor.WHITE + "\n" + "End location: " + ChatColor.AQUA + pointCounter.config.get("coordinates.x.end") + ", " + pointCounter.config.get("coordinates.z.end"));

        return true;
    }
}
