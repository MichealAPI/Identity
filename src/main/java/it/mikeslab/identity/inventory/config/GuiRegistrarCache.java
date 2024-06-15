package it.mikeslab.identity.inventory.config;

import it.mikeslab.identity.inventory.InventoryType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

@Getter
@RequiredArgsConstructor
public class GuiRegistrarCache {

    private final InventoryType inventoryType;

    private final ConfigurationSection section;

}
