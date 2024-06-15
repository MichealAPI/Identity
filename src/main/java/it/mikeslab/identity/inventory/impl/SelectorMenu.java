package it.mikeslab.identity.inventory.impl;

import it.mikeslab.commons.api.inventory.ConsumerFilter;
import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.config.GuiConfig;
import it.mikeslab.commons.api.inventory.config.GuiConfigImpl;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.inventory.util.PageSystem;
import it.mikeslab.commons.api.logger.LoggerUtil;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.CustomInventory;
import it.mikeslab.identity.inventory.InventoryContext;
import it.mikeslab.identity.inventory.InventoryType;
import it.mikeslab.identity.pojo.Identity;
import it.mikeslab.identity.pojo.InventorySettings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SelectorMenu implements CustomInventory {

    private static final String SUB_FOLDER = "inventories";

    private final IdentityPlugin instance;

    private final String fieldIdentifier;
    private final String fileName;

    private final InventoryContext context;

    @Getter
    private final InventoryType inventoryType;

    @Getter
    private int id;

    @Getter @Setter
    private boolean completed;


    public SelectorMenu(final IdentityPlugin instance, InventorySettings settings) {
        this.instance = instance;
        this.fieldIdentifier = settings.getFieldIdentifier();
        this.fileName = settings.getFileName();
        this.inventoryType = settings.getInventoryType();

        this.context = new InventoryContext();

        this.context.setConsumers(
                this.getConsumers()
        );

        this.generate();


    }


    @Override
    public void show(Player player) {

        CustomGui customGui = instance.getGuiFactory().getCustomGui(id);

        if(customGui == null) return;

        instance.getGuiFactory().open(
                player,
                id
        );

    }

    @Override
    public void generate() {

        String fileName = SUB_FOLDER + File.separator + this.fileName + ".yml";
        GuiConfig guiConfig;

        // Check if the guiConfig is cached, this
        // allows to prevent loading the same config multiple times
        if(instance.getCachedGuiConfig().containsKey(fileName)) {

            guiConfig = instance.getCachedGuiConfig().get(fileName);

        } else {

            guiConfig = new GuiConfigImpl(instance);
            guiConfig.loadConfig(fileName, true);

            instance.getCachedGuiConfig().put(fileName, guiConfig);
        }

        // Set up the context
        // Get the default gui details
        this.context.setDefaultGuiDetails(guiConfig.getGuiDetails(
                Optional.empty(),
                this.getConsumers()
        ));

        this.id = instance.getGuiFactory().create(this.context.getDefaultGuiDetails());

    }

    @Override
    public Optional<Map<String, Consumer<GuiInteractEvent>>> getConsumers() {

        Map<String, Consumer<GuiInteractEvent>> result = new HashMap<>();

        result.put(ConsumerFilter.ANY.getFilter(), event -> {

            GuiElement clickedElement = event.getClickedElement();
            String internalValue = clickedElement.getInternalValue();

            if(internalValue == null) return;

            if(internalValue.startsWith("select:")) {

                String[] split = internalValue.split(":");
                String value = split[1];

                UUID uuid = event.getWhoClicked().getUniqueId();

                this.handleSelection(uuid, value);

                Bukkit.getScheduler().runTask(instance, () -> {

                    CustomInventory fallbackInventory = instance.getGuiConfigRegistrar()
                            .getPlayerInventories()
                            .get(uuid)
                            .get(instance.getGuiConfigRegistrar().getFallbackGuiIdentifier());

                    fallbackInventory.show(event.getWhoClicked());

                });

            }

        });

        return Optional.of(result);
    }

    @Override
    public void setPlaceholders(Player player, GuiDetails guiDetails) {

    }

    /**
     * Handle the selection of a value
     * @param uuid The uuid of the player
     * @param selectedValue The selected value
     */
    private void handleSelection(UUID uuid, String selectedValue) {

        Identity identity = instance.getSetupCacheHandler().getIdentity(uuid);
        if(identity == null) {
            LoggerUtil.log(
                    IdentityPlugin.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.UTIL,
                    "Could not find identity for player with UUID: " + uuid + " during setup."
            );
            return;
        }

        // Update the identity
        instance.getSetupCacheHandler().updateIdentity(
                uuid,
                new AbstractMap.SimpleEntry<>(
                        fieldIdentifier,
                        selectedValue
                )
        );

        // Set the completed flag
        this.setCompleted(true);

    }


}
