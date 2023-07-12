package it.mikeslab.identity.utils.furnace;

import com.cryptomorin.xseries.XMaterial;
import it.mikeslab.identity.disk.Settings;
import it.mikeslab.identity.utils.FormatUtils;
import it.mikeslab.identity.utils.Legacy;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FurnaceGui implements Listener {
    private ArrayList<FurnaceElement> elements;
    private Component title;
    private Inventory inventory;
    private JavaPlugin plugin;

    public FurnaceGui(Component title, JavaPlugin plugin) {
        this.elements = new ArrayList<>();
        this.title = title;
        this.plugin = plugin;
    }



    public void addElement(FurnaceElement element) {
        this.elements.add(element);
    }

    public void setElement(FurnaceElement element) {
        this.elements.set(element.getLocation(), element);
    }

    public void removeElement(FurnaceElement element) {
        this.elements.remove(element);
    }

    public FurnaceGui build() {

        Inventory inventory = Bukkit.createInventory(null, InventoryType.FURNACE, Legacy.translate(title));

        for(int i = 0; i < elements.size(); i++) {
            inventory.setItem(i, FurnaceManager.edit(elements.get(i).getStack(), elements.get(i).getCustomModelData(), elements.get(i).getDisplayname(), elements.get(i).getLore(), this));
        }


        this.inventory = inventory;
        return this;
    }


    public void show(Player player) {
        player.openInventory(inventory);
    }


    public ItemStack getCustomStack(int age) {
        if(plugin.getConfig().getConfigurationSection("inventories.age.data.per-age-item") != null) {
            ConfigurationSection years = plugin.getConfig().getConfigurationSection("inventories.age.data.years");
            if(years.isConfigurationSection(String.valueOf(age))) {
                ItemStack stack = XMaterial.matchXMaterial(years.getString(age + ".material")).get().parseItem();
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(Legacy.translate(MiniMessage.miniMessage().deserialize(years.getString(age + ".name").replace("%actualage%", String.valueOf(age))).decoration(TextDecoration.ITALIC, false)));
                List<Component> lore = Settings.translate(FormatUtils.replaceList(years.getStringList(age + ".lore"), String.valueOf(age), "%actualage%"));
                meta.setLore(Legacy.translate(lore));
                meta.setCustomModelData(years.getInt(age + ".custom-model-data"));
                stack.setItemMeta(meta);
                return stack;
            }
        }
        return null;
    }








}



