package it.mikeslab.identity.utils;


import it.mikeslab.identity.inventories.InventoryType;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@AllArgsConstructor
public class GUIHolder implements InventoryHolder, Listener {
    InventoryType inventory;

    @Override
    public Inventory getInventory() {
        return null;
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getOpenInventory().getTopInventory().getHolder() instanceof GUIHolder) {
                player.closeInventory();
            }
        }
    }

}

