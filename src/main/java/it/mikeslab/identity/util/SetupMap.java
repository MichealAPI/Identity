package it.mikeslab.identity.util;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.CustomInventory;
import it.mikeslab.commons.api.inventory.helper.InventoryMap;
import it.mikeslab.identity.IdentityPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A map that stores the setup inventories for each player
 * Necessary to keep track of the inventories that are open
 */
public class SetupMap extends InventoryMap {

    private static final int EXPIRATION_TIME = 1000; // seconds = n/1000

    private final IdentityPlugin instance;
    private final Map<UUID, Map<String, Long>> lastAccessMap = new HashMap<>();

    public SetupMap(IdentityPlugin instance) {
        this.instance = instance;
    }

    /**
     * Force the expiration of all inventories for a player
     * that leads to a necessary re-population of each inventory
     * @param uuid The player UUID
     */
    public void forceExpiration(UUID uuid) {
        for (String keyId : this.getCachedInventories(uuid).keySet()) {
            this.forceExpiration(uuid, keyId);
        }
    }

    public void forceExpiration(UUID uuid, String keyId) {
        this.setLastAccess(uuid, keyId, -2L);
    }

    /**
     * Load the setup inventories for a player
     *
     * @param uuid The player UUID
     */
    public void load(UUID uuid) {

        Map<String, CustomInventory> inventoryMap = new HashMap<>(
                instance.getGuiConfigRegistrar().getGuis() // Recreates a CustomInventory instance for each player
        );

        this.putAll(uuid, inventoryMap);

    }


    public CustomInventory getInventory(UUID playerUUID, String inventoryId) {

        if (!this.containsKey(playerUUID)) {

            this.load(playerUUID);

            // Register the last access time
            this.setLastAccess(
                    playerUUID,
                    inventoryId,
                    System.currentTimeMillis()
            );

            return this.getCachedInventories(playerUUID).get(inventoryId);
        }

        CustomInventory customInventory = this.getCachedInventories(playerUUID).get(inventoryId);
        long lastAccess = this.lastAccessMap.get(playerUUID).getOrDefault(inventoryId, -1L);

        boolean expired = System.currentTimeMillis() - lastAccess > EXPIRATION_TIME;

        if(expired) {

            if(customInventory.getId() == -1) return customInventory;

            CustomGui customGui = customInventory.getCustomGui();

            customGui.setOwnerUUID(playerUUID);

            if(customGui.getInventory() == null) {
                customGui.generateInventory();
                return customInventory;
            }

            customGui.populateInventory();

            this.setLastAccess(
                    playerUUID,
                    inventoryId,
                    System.currentTimeMillis()
            );

        }

        return customInventory;
    }


    public void setLastAccess(UUID playerUUID, String inventoryId, long value) {
        Map<String, Long> lastAccess = this.lastAccessMap.getOrDefault(playerUUID, new HashMap<>());

        lastAccess.put(inventoryId, value);

        this.lastAccessMap.put(playerUUID, lastAccess);
    }


    public boolean containsInventory(UUID playerUUID, String arg) {
        return this.getCachedInventories(playerUUID).containsKey(arg);
    }

    public void clearInventoryMap() {
        this.clear();
        this.lastAccessMap.clear();
    }

}
