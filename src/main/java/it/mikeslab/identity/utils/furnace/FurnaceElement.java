package it.mikeslab.identity.utils.furnace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FurnaceElement {
    private Component displayname;
    private List<Component> lore;
    private int location;
    private Consumer<InventoryClickEvent> clickEvent;
    private ItemStack stack;
    private boolean dynamic;
    private int customModelData;

}
