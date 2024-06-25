package it.mikeslab.identity.util;

import it.mikeslab.commons.api.inventory.util.CustomInventory;
import it.mikeslab.commons.api.inventory.util.InventoryMap;
import it.mikeslab.identity.IdentityPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A map that stores the setup inventories for each player
 * Necessary to keep track of the inventories that are open
 */
public class SetupMap extends InventoryMap {

    private static final int EXPIRATION_TIME = 10000; // seconds = n/1000

    private final IdentityPlugin instance;
    private final Map<UUID, Long> lastAccessMap = new HashMap<>();

    public SetupMap(IdentityPlugin instance) {
        super();

        this.instance = instance;
    }

    public void forceExpiration(UUID uuid) {
        this.lastAccessMap.put(uuid, -1L);
    }

    public Map<String, CustomInventory> getInventories(UUID uuid) {

        long lastAccess = this.lastAccessMap.getOrDefault(uuid, -1L);

        boolean firstAccess = lastAccess == -1L;
        boolean expired = System.currentTimeMillis() - lastAccess > EXPIRATION_TIME;

        if ((!this.containsKey(uuid) && firstAccess) || expired) {

            // Register the last access time
            this.lastAccessMap.put(uuid, System.currentTimeMillis());

            return this.load(uuid);
        }



        return this.get(uuid);
    }

    /**
     * Load the setup inventories for a player
     * @param uuid The player UUID
     */
    public Map<String, CustomInventory> load(UUID uuid) {

        Map<String, CustomInventory> inventoryMap = new HashMap<>(
                instance.getGuiConfigRegistrar().getGuis() // Recreates a CustomInventory instance for each player
        );

        this.put(uuid, inventoryMap);

        return inventoryMap;
    }


}
