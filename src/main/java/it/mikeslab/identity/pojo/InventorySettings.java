package it.mikeslab.identity.pojo;

import it.mikeslab.identity.inventory.InventoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@AllArgsConstructor
public class InventorySettings {

    private String fieldIdentifier;

    // private final int id;

    private String fileName; // The file name of the inventory

    private boolean closeOnFail; // If the inventory should be closed if the checks fail

    private InventoryType inventoryType; // The type of the inventory

}
