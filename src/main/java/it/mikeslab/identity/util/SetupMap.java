package it.mikeslab.identity.util;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.util.CustomInventory;
import it.mikeslab.commons.api.inventory.util.InventoryMap;
import it.mikeslab.identity.IdentityPlugin;

import java.util.*;

/**
 * A map that stores the setup inventories for each player
 * Necessary to keep track of the inventories that are open
 */
public class SetupMap extends InventoryMap {

    private static final int EXPIRATION_TIME = 10000; // seconds = n/1000

    private final IdentityPlugin instance;
    private final Map<UUID, Map<String, Long>> lastAccessMap = new HashMap<>();

    public SetupMap(IdentityPlugin instance) {
        super();

        this.instance = instance;
    }

    /**
     * Force the expiration of all inventories for a player
     * that leads to a necessary re-population of each inventory
     * @param uuid The player UUID
     */
    public void forceExpiration(UUID uuid) {
        for (String keyId : this.get(uuid).keySet()) {
            this.forceExpiration(uuid, keyId);
        }
    }

    public void forceExpiration(UUID uuid, String keyId) {
        this.setLastAccess(uuid, keyId, -2L);
    }

//    public Map<String, CustomInventory> getInventories(UUID uuid) {
//
//        boolean firstAccess = lastAccess == -1L;
//        boolean expired = System.currentTimeMillis() - lastAccess > EXPIRATION_TIME;
//
//        if (!this.containsKey(uuid)) {
//
//            // Register the last access time
//            this.lastAccessMap.put(uuid, System.currentTimeMillis());
//
//            return this.load(uuid);
//        }
//
//        if (expired && this.containsKey(uuid)) {
//
//            this.get(uuid).values().forEach(customInventory -> {
//
//                if(customInventory.getId() == -1) {
//                    return;
//                }
//
//                System.out.println("\n\n\n\n\n" + customInventory.getInventoryType());
//
//                System.out.println(customInventory.getCustomGui());
//
//                CustomGui customGui = customInventory.getCustomGui();
//
//                customGui.setOwnerUUID(uuid);
//
//                if(customGui.getInventory() == null) {
//                    customGui.generateInventory();
//                    return;
//                }
//
//                customGui.populateInventory();
//
//            });
//
//
//        }
//
//
//        return this.get(uuid);
//    }

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


    public CustomInventory getInventory(UUID playerUUID, String inventoryId) {

        if (!this.containsKey(playerUUID)) {

            this.load(playerUUID);

            // Register the last access time
            this.setLastAccess(
                    playerUUID,
                    inventoryId,
                    System.currentTimeMillis()
            );

            return this.get(playerUUID).get(inventoryId);
        }

        CustomInventory customInventory = this.get(playerUUID).get(inventoryId);
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
        return this.get(playerUUID).containsKey(arg);
    }


}
