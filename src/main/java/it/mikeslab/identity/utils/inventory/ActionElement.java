package it.mikeslab.identity.utils.inventory;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ActionElement {

    @Getter private final char charPos;
    @Getter private final int intPos;
    @Getter private final Action action;
    @Getter @Setter private ItemStack stack;
    @Getter private final Component displayname;
    @Getter private final List<Component> lore;


    public ActionElement(final int intPos, final char charPos, final Action action, final ItemStack stack, final Component displayname, final List<Component> lore) {
        this.charPos = charPos;
        this.action = action;
        this.stack = stack;
        this.displayname = displayname;
        this.lore = lore;
        this.intPos = intPos;
    }


}
