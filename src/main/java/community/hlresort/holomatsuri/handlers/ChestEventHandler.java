package community.hlresort.holomatsuri.handlers;

import community.hlresort.holomatsuri.pointCounter;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ChestEventHandler implements Listener {
    public static List<UUID> trackedAddPlayers = new ArrayList<>();
    public static List<UUID> trackedRemovePlayers = new ArrayList<>();
    HashMap<Location, List<ItemStack>> cachedInventories = new HashMap<>();

    private boolean chestTracked(Location location) {
        boolean exists = false;

        try {
            Statement existsStatement = pointCounter.conn.createStatement();
            ResultSet existsResult = existsStatement.executeQuery("SELECT EXISTS(SELECT 1 FROM chests WHERE x=\"" + location.getBlockX() + "\" AND y=\"" + location.getBlockY() + "\" AND z=\"" + location.getBlockZ() + "\" AND world=\"" + location.getWorld().getName() + "\");");
            while (existsResult.next()) {
                exists = existsResult.getBoolean(1);
            }
            existsStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return exists;
    }

    private void updateCachedInventory(Inventory inventory) {
        Location location = inventory.getLocation();
        if (location == null) return;
        boolean exists = chestTracked(location);

        if (exists) {
            List<ItemStack> clonedItemStack = new ArrayList<>();
            ItemStack[] knownItemStack = inventory.getContents();
            for (ItemStack itemStack : knownItemStack) {
                if (itemStack == null) clonedItemStack.add(null);
                else clonedItemStack.add(new ItemStack(itemStack.getType(), itemStack.getAmount()));
            }
            cachedInventories.put(location, clonedItemStack);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        HumanEntity player = event.getPlayer();
        Location location = event.getInventory().getLocation();
        if (location == null || event.getInventory().getType() != InventoryType.CHEST) return;

        boolean exists = chestTracked(location);
        int trackedAddIndex = trackedAddPlayers.indexOf(player.getUniqueId());
        int trackedRemoveIndex = trackedRemovePlayers.indexOf(player.getUniqueId());
        if (trackedAddIndex != -1) {
            if (exists) {
                player.sendMessage("That chest is already tracked!");
            } else {
                try {
                    Statement addChest = pointCounter.conn.createStatement();
                    addChest.executeUpdate("INSERT INTO chests(x, y, z, world) VALUES(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ", \"" + location.getWorld().getName() + "\");");
                    addChest.close();
                    player.sendMessage("Chest has been added!");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    player.sendMessage("Failed to add the chest, database error.");
                }
            }
            trackedAddPlayers.remove(trackedAddIndex);
        } else if (trackedRemoveIndex != -1) {
            if (!exists) {
                player.sendMessage("That chest isn't tracked!");
            } else {
                try {
                    Statement addChest = pointCounter.conn.createStatement();
                    addChest.executeUpdate("DELETE FROM chests WHERE x = " + location.getBlockX() + " AND y = " + location.getBlockY() + " AND z = " + location.getBlockZ() + " AND world = \"" + location.getWorld().getName() + "\";");
                    addChest.close();
                    player.sendMessage("Chest has been removed!");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    player.sendMessage("Failed to remove the chest, database error.");
                }
            }
            trackedAddPlayers.remove(trackedRemoveIndex);
        }
        updateCachedInventory(event.getInventory());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        updateCachedInventory(event.getInventory());
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        Location location = event.getInventory().getLocation();
        if (location == null || event.getInventory().getType() != InventoryType.CHEST) return;

        boolean exists = chestTracked(location);
        if (exists) {
            List<ItemStack> oldInventory = cachedInventories.get(location);
            List<String> queries = new ArrayList<>();

            pointCounter.plugin.getServer().getScheduler().runTask(pointCounter.plugin, () -> {
                ItemStack[] newInventory = event.getInventory().getContents();
                if (oldInventory != null) {
                    for (int i = 0; i < newInventory.length; i++) {
                        if (newInventory[i] != oldInventory.get(i)) {
                            if (newInventory[i] == null || newInventory[i].getType().isAir()) {
                                if (!oldInventory.get(i).getType().isBlock()) return;
                                queries.add(oldInventory.get(i).getType().name() + " = " + oldInventory.get(i).getType().name() + " - " + oldInventory.get(i).getAmount());
                            } else if (oldInventory.get(i) == null || oldInventory.get(i).getType().isAir()) {
                                if (!newInventory[i].getType().isBlock()) return;
                                queries.add(newInventory[i].getType().name() + " = " + newInventory[i].getType().name() + " + " + newInventory[i].getAmount());
                            } else {
                                if (newInventory[i].getType() == oldInventory.get(i).getType()) {
                                    if(!newInventory[i].getType().isBlock() || newInventory[i].getType().isAir()) return;
                                    int amountDifference = newInventory[i].getAmount() - oldInventory.get(i).getAmount();
                                    if(amountDifference != 0) queries.add(newInventory[i].getType().name() + " = " + newInventory[i].getType().name() + " + " + amountDifference);
                                } else {
                                    if(oldInventory.get(i).getType().isBlock() && !oldInventory.get(i).getType().isAir()) queries.add(oldInventory.get(i).getType().name() + " = " + oldInventory.get(i).getType().name() + " - " + oldInventory.get(i).getAmount());
                                    if(newInventory[i].getType().isBlock() && !newInventory[i].getType().isAir()) queries.add(newInventory[i].getType().name() + " = " + newInventory[i].getType().name() + " + " + newInventory[i].getAmount());
                                }
                            }
                        }
                    }
                    if (queries.size() > 0) {
                        try {
                            Statement statement = pointCounter.conn.createStatement();
                            ResultSet result = statement.executeQuery("SELECT EXISTS(SELECT 1 FROM delivery WHERE uuid=\"" + player.getUniqueId() + "\");");
                            while (result.next()) {
                                if (result.getBoolean(1)) {
                                    Statement updateRow = pointCounter.conn.createStatement();
                                    for(String query : queries) {
                                        updateRow.executeUpdate("UPDATE delivery SET " + query + " WHERE uuid=\"" + player.getUniqueId() + "\";");
                                    }
                                    updateRow.close();
                                } else {
                                    Statement insertRow = pointCounter.conn.createStatement();
                                    insertRow.executeUpdate("INSERT INTO delivery(uuid) VALUES (\"" + player.getUniqueId() + "\");");
                                    for(String query : queries) {
                                        insertRow.executeUpdate("UPDATE delivery SET " + query + " WHERE uuid=\"" + player.getUniqueId() + "\";");
                                    }
                                    insertRow.close();
                                }
                            }
                            statement.close();
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                updateCachedInventory(event.getInventory());
            });
        }
    }
}
