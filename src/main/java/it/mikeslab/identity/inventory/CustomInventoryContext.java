package it.mikeslab.identity.inventory;

import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.pojo.InventoryContext;
import it.mikeslab.identity.inventory.pojo.InventorySettings;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomInventoryContext {

    private IdentityPlugin instance;

    private InventoryContext inventoryContext;

    private InventorySettings settings;

    private int id = -1; // Default is -1 to indicate that it is not set

    private boolean completed; // Ignored for the main menu

    public CustomInventoryContext(final IdentityPlugin instance, InventorySettings settings) {
        this.instance = instance;
        this.settings = settings;
    }

}
