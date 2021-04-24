package community.hlresort.holomatsuri.commands;

import community.hlresort.holomatsuri.ChestEventHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class addChest implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player!");
            return false;
        }

        Player player = (Player) sender;

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
