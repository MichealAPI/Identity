package it.mikeslab.identity.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class ClickableItem {

    private final ItemStack displayItem;
    private final String value;

}
