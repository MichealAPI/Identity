package it.mikeslab.identity.inventory;

import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface CustomInventory {

    /**
     * Get the id of the gui
     * @return The id of the gui
     */
    default int getId() {
        return -1;
    }

    /**
     * Show the gui to the player
     * @param player The player
     */
    void show(Player player);

    /**
     * Initialize the gui
     */
    void generate();

    /**
     * Get the consumers of the gui
     * @return The consumers of the gui
     */
    Optional<Map<String, Consumer<GuiInteractEvent>>> getConsumers();

    /**
     * Set the placeholders of the gui, applied to each element
     * @param player The player
     * @param guiDetails The details of the gui
     */
    void setPlaceholders(Player player, GuiDetails guiDetails);

    /**
     * If the setup for the gui is completed
     * @return If the setup for the gui is completed
     */
    boolean isCompleted();

    /**
     * Get the inventory type of the gui
     * @return The inventory type of the gui
     */
    InventoryType getInventoryType();


}
