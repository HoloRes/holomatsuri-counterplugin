package community.hlresort.holomatsuri.commands;

import community.hlresort.holomatsuri.handlers.ChestEventHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class addChest implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player!");
            return false;
        }

        int foundIndex = ChestEventHandler.trackedAddPlayers.indexOf(player.getUniqueId());
        if (foundIndex != -1) {
            ChestEventHandler.trackedAddPlayers.remove(foundIndex);
            player.sendMessage("Cancelled action.");
        } else {
            ChestEventHandler.trackedAddPlayers.add(player.getUniqueId());
            player.sendMessage("Open the chest that you want to get tracked.\nRun this command again to cancel.");
        }

        return true;
    }
}
