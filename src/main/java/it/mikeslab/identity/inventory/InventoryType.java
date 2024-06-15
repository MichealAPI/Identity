package it.mikeslab.identity.inventory;

import org.jetbrains.annotations.Nullable;

/**
 * The types of inventories that can be opened
 */
public enum InventoryType {

    INPUT,
    SELECTOR,
    VALUE,
    MAIN;

    @Nullable
    public static InventoryType fromString(String type) {
        for (InventoryType value : values()) {
            if (value.name().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

}
