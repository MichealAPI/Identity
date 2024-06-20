package it.mikeslab.identity.inventory.pojo;

import it.mikeslab.identity.inventory.InventoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;

@Data
@Builder
@AllArgsConstructor
public class InventorySettings {

    private String fieldIdentifier;

    private Path relativePath; // The file name of the inventory

    private boolean closeOnFail; // If the inventory should be closed if the checks fail

    private InventoryType inventoryType; // The type of the inventory

}
