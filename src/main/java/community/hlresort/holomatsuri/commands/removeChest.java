package community.hlresort.holomatsuri.commands;

import community.hlresort.holomatsuri.handlers.ChestEventHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class removeChest implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player!");
            return false;
        }

        int foundIndex = ChestEventHandler.trackedRemovePlayers.indexOf(player.getUniqueId());
        if (foundIndex != -1) {
            ChestEventHandler.trackedRemovePlayers.remove(foundIndex);
            player.sendMessage("Cancelled action.");
        } else {
            ChestEventHandler.trackedRemovePlayers.add(player.getUniqueId());
            player.sendMessage("Open the chest that you want not tracked anymore.\nRun this command again to cancel.");
        }

        return true;
    }
}
