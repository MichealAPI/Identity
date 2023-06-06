package it.myke.identity.utils.furnace;

import it.myke.identity.utils.Legacy;
import it.myke.identity.utils.PersonUtil;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Data
public class FurnaceManager implements Listener {
    private final JavaPlugin plugin;
    public static Map<FurnaceGui, TaskObject> taskMap;
    public static Map<UUID, FurnaceGui> furnaceGuis;

    public static PersonUtil personUtil;

    public FurnaceManager(JavaPlugin plugin, PersonUtil personUtil) {
        this.plugin = plugin;
        furnaceGuis = new HashMap<>();
        taskMap = new HashMap<>();

        FurnaceManager.personUtil = personUtil;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static boolean isRegistered(UUID uuid) {
        return furnaceGuis.containsKey(uuid);
    }
    public static void addFurnace(UUID uuid, FurnaceGui furnaceGui) {
        furnaceGuis.put(uuid, furnaceGui);
        taskMap.put(furnaceGui,TaskObject.defaultObject());
    }

    public static void removeFurnace(UUID uuid, FurnaceGui furnaceGui) {
        furnaceGuis.remove(uuid);
        taskMap.remove(furnaceGui);
    }

    private boolean isUUIDRegistered(UUID uuid) {
        return furnaceGuis.containsKey(uuid);
    }


    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(isUUIDRegistered(player.getUniqueId())) {
                player.closeInventory();
            }
        }
    }


    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if(furnaceGuis.get(player.getUniqueId()) == null) return;
        if(!personUtil.isPerson(event.getPlayer().getUniqueId())) return;
        FurnaceGui furnaceGui = furnaceGuis.get(player.getUniqueId());

        if(!taskMap.containsKey(furnaceGui)) return;
        if(taskMap.get(furnaceGui).isCompleted()) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> furnaceGui.show((Player) event.getPlayer()), 1L);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(furnaceGuis.get(player.getUniqueId()) == null) return;

        FurnaceGui furnaceGui = furnaceGuis.get(player.getUniqueId());

        event.setCancelled(true);

        for(FurnaceElement element : furnaceGui.getElements()) {
            if(element.getLocation() == event.getSlot()) {
                element.getClickEvent().accept(event);
            }

            if(element.isDynamic()) {
                int age = taskMap.getOrDefault(furnaceGui, TaskObject.defaultObject()).getAge();
                ItemStack customStack = furnaceGui.getCustomStack(age);
                ItemStack stack = customStack != null ? customStack : edit(element.getStack(), this.getCustomModelData(age), element.getDisplayname(), element.getLore(), furnaceGui);
                event.getClickedInventory().setItem(element.getLocation(), stack);
                ((Player) event.getWhoClicked()).updateInventory();
            }
        }
    }

    public int getCustomModelData(int age) {
        if(plugin.getConfig().getConfigurationSection("inventories.age.data.years") != null) {
            ConfigurationSection years = plugin.getConfig().getConfigurationSection("inventories.age.data.years");
            if(years.isConfigurationSection(String.valueOf(age))) {
                return years.getInt(age + ".custom-model-data");
            }
        }
        return -1;
    }



    static ItemStack edit(ItemStack itemStack, int customModelData, Component displayName, List<Component> lore, FurnaceGui furnaceGui) {
        int age = taskMap.getOrDefault(furnaceGui, TaskObject.defaultObject()).getAge();

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Legacy.translate(displayName.replaceText(TextReplacementConfig.builder().matchLiteral("%actualage%").replacement(String.valueOf(age)).build()).decoration(TextDecoration.ITALIC, false)));
        if(lore != null) {
            List<Component> finalLore = new ArrayList<>();
            for(Component line : lore) {
                finalLore.add(line.replaceText(TextReplacementConfig.builder().matchLiteral("%actualage%").replacement(String.valueOf(age)).build()).decoration(TextDecoration.ITALIC, false));
            }

            itemMeta.setLore(Legacy.translate(finalLore));
        }

        if(customModelData != -1) {
            itemMeta.setCustomModelData(customModelData);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }






}
