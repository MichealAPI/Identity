package it.mikeslab.identity.util;

import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.InventoryType;
import it.mikeslab.identity.inventory.CustomInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A map that stores the setup inventories for each player
 * Necessary to keep track of the inventories that are open
 */
public class InventoryMap implements Map<UUID, Map<String, CustomInventory>> {

    private final Map<UUID, Map<String, CustomInventory>> map = new HashMap<>();
    private final IdentityPlugin instance;

    public InventoryMap(IdentityPlugin instance) {
        super();

        this.instance = instance;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Map<String, CustomInventory> get(Object key) {

        if (!map.containsKey(key)) {

            UUID uuid = (UUID) key;

            Map<String, CustomInventory> inventoryMap = new HashMap<>(
                    instance.getGuiConfigRegistrar().getGuis() // Recreates a CustomInventory instance for each player
            );

            map.put(uuid, inventoryMap);

            return inventoryMap;
        }


        return map.get(key);
    }

    @Nullable
    @Override
    public Map<String, CustomInventory> put(UUID key, Map<String, CustomInventory> value) {
        return map.put(key, value);
    }

    @Override
    public Map<String, CustomInventory> remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends UUID, ? extends Map<String, CustomInventory>> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    @Override
    public Set<UUID> keySet() {
        return map.keySet();
    }

    @NotNull
    @Override
    public Collection<Map<String, CustomInventory>> values() {
        return map.values();
    }

    @NotNull
    @Override
    public Set<Entry<UUID, Map<String, CustomInventory>>> entrySet() {
        return map.entrySet();
    }
}
