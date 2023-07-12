package it.mikeslab.identity.utils.inventory;

import com.cryptomorin.xseries.XMaterial;
import it.mikeslab.identity.utils.config.CustomConfigsInit;
import it.mikeslab.identity.disk.Settings;
import it.mikeslab.identity.utils.CustomHeads;
import it.mikeslab.identity.utils.Legacy;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

public class InventoryBuilder {
    private final String chars = "abcdefghijklmnopqrstuvwxyzA";
    @Getter private ArrayList<ActionElement> elements;
    @Getter private ItemStack filler;
    @Getter private Component title;


    public InventoryBuilder getBuilder(CustomConfigsInit customConfigsInit, String inventory) {
        elements = new ArrayList<>();
        title = MiniMessage.miniMessage().deserialize(customConfigsInit.getInventoriesConfig().getString(inventory + ".title"));
        for (String s : customConfigsInit.getInventoriesConfig().getConfigurationSection(inventory + ".elements").getKeys(false)) {
            ConfigurationSection elementSection = customConfigsInit.getInventoriesConfig().getConfigurationSection(inventory + ".elements." + s);
            Action action = getActionOrDefault(elementSection);
            int intPosition = Integer.parseInt(elementSection.getName());
            char position = translate(Integer.parseInt(elementSection.getName()));
            elements.add(new ActionElement(intPosition, position, action, parseStack(elementSection), MiniMessage.miniMessage().deserialize(elementSection.getString("name")).decoration(TextDecoration.ITALIC, false), Settings.translate(elementSection.getStringList("lore"))));
        }
        String fillerPath = customConfigsInit.getInventoriesConfig().getString(inventory + ".filler");
        if(fillerPath != null) {
            ItemStack filler = XMaterial.matchXMaterial(fillerPath).get().parseItem();
            ItemMeta meta = filler.getItemMeta();
            meta.setCustomModelData(customConfigsInit.getInventoriesConfig().getInt(inventory + ".filler-data"));
            filler.setItemMeta(meta);

            this.filler = filler;
        }
        return this;
    }


    private ItemStack parseStack(ConfigurationSection elementSection) {
        if(!Objects.equals(elementSection.getString("material"), "CUSTOM_HEAD")) {
            ItemStack stack = XMaterial.matchXMaterial(elementSection.getString("material")).get().parseItem();
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(Legacy.translate(MiniMessage.miniMessage().deserialize(elementSection.getString("name")).decoration(TextDecoration.ITALIC, false)));
            meta.setLore(Legacy.translate(Settings.translate(elementSection.getStringList("lore"))));
            meta.setCustomModelData(elementSection.getInt("custom-model-data"));
            stack.setItemMeta(meta);
            return stack;
        }

        if(elementSection.contains("texture")) {
            return CustomHeads.getCustomHead(elementSection.getString("texture"), MiniMessage.miniMessage().deserialize(elementSection.getString("name")), Settings.translate(elementSection.getStringList("lore")));
        } else Bukkit.getLogger().severe("Texture value not found for " + elementSection.getName());

        Bukkit.getLogger().severe("Error while parsing inventory element: " + elementSection.getName() + " in inventory: " + elementSection.getParent().getName());
        return null;
    }


    private Action getActionOrDefault(ConfigurationSection elementSection) {
        if(elementSection.contains("action")) {
            return Action.valueOf(elementSection.getString("action").toUpperCase());
        }
        return Action.NONE;
    }

    public char translate(final int i) {
        return chars.charAt(i);
    }

}


