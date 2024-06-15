package it.mikeslab.identity.inventory.config;

import it.mikeslab.commons.api.logger.LoggerUtil;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.CustomInventory;
import it.mikeslab.identity.inventory.InventoryType;
import it.mikeslab.identity.inventory.impl.MainMenu;
import it.mikeslab.identity.inventory.impl.SelectorMenu;
import it.mikeslab.identity.pojo.InventorySettings;
import it.mikeslab.identity.util.InventoryMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class GuiConfigRegistrar {

    private final IdentityPlugin instance;
    private final ConfigurationSection section;

    @Getter
    private String fallbackGuiIdentifier;

    @Getter
    private final InventoryMap playerInventories;

    private final Map<String, GuiRegistrarCache> cache = new HashMap<>();

    @Getter
    private final List<String> mandatoryInventories = new ArrayList<>();


    public GuiConfigRegistrar(IdentityPlugin instance, ConfigurationSection section) {
        this.instance = instance;
        this.section = section;

        this.playerInventories = new InventoryMap(instance);
    }

    public Map<String, CustomInventory> getGuis() {

        Map<String, CustomInventory> guis = new HashMap<>();

        for(Map.Entry<String, GuiRegistrarCache> entry : cache.entrySet()) {

            String keyId = entry.getKey();
            GuiRegistrarCache cache = entry.getValue();

            guis.put(
                    keyId,
                    createInventory(
                            cache.getInventoryType(),
                            cache.getSection().getString(ConfigField.PATH.getField),
                            keyId
                    )
            );

        }

        return guis;
    }


    public void register() {

        for(String key : section.getKeys(false)) {

            ConfigurationSection configSection = section.getConfigurationSection(key);

            if (configSection == null) {
                continue;
            }

            String typeAsString = configSection.getString(ConfigField.TYPE.getField);

            if (typeAsString == null) {
                continue;
            }

            InventoryType type = InventoryType.fromString(typeAsString);

            if(type == null) {
                continue;
            }

            // if the inventory should be kept open until completed
            boolean mandatory = configSection.getBoolean(ConfigField.MANDATORY.getField, false); // mandatory is defaulted to false

            if(mandatory && type == InventoryType.MAIN) {
                LoggerUtil
                        .log(
                                IdentityPlugin.PLUGIN_NAME,
                                Level.WARNING,
                                LoggerUtil.LogSource.CONFIG,
                                "Inventory '" + key + "' is marked as mandatory but is a main menu"
                        );
                continue;
            }

            CustomInventory customInventory = createInventory(
                    type,
                    configSection.getString(ConfigField.PATH.getField),
                    key
            );

            if(customInventory == null) {
                continue;
            }

            if(type == InventoryType.MAIN) {
                this.fallbackGuiIdentifier = key;
            }

            this.cache.put(key, new GuiRegistrarCache(type, configSection));

            // If it's a mandatory inventory, key is added to list
            if(mandatory) {
                mandatoryInventories.add(key);
            }

        }

        if(fallbackGuiIdentifier == null) {
            LoggerUtil
                    .log(
                            IdentityPlugin.PLUGIN_NAME,
                            Level.SEVERE,
                            LoggerUtil.LogSource.CONFIG,
                            "No main menu found in the configuration"
                    );

            Bukkit.getPluginManager().disablePlugin(instance);
        }



    }


    private CustomInventory createInventory(InventoryType type, String path, String id) {

        // Replace the path with the correct separator
        path = path.replace("\\", File.separator) // todo should I remove this?
                .replace("/", File.separator);

        // Get the file
        File file = new File(instance.getDataFolder(), path); // todo add default sub-path /inventories

        // Check if the file exists
        if(!file.exists()) {
            LoggerUtil
                    .log(
                            IdentityPlugin.PLUGIN_NAME,
                            Level.WARNING,
                            LoggerUtil.LogSource.CONFIG,
                            "File at '" + file.getAbsolutePath() + "' does not exist"
                    );
            return null;
        }

        // Create the gui config

        switch (type) {

            case SELECTOR:

                return new SelectorMenu(
                        instance,
                        new InventorySettings(
                                id,
                                file.getName().replace(".yml", ""),
                                true,
                                type
                        ));
            case MAIN:

                return new MainMenu(
                        instance,
                        new InventorySettings( // todo this should be extracted, it is not just because of the close on fail personalization, to be fixed.
                                id,
                                file.getName().replace(".yml", ""),
                                true,
                                type
                        )
                );

            // break;

            default: return null;

            // todo other cases

        }

    }

    /**
     * Get the custom inventory key string identifier from the custom inventory id
     * @param id The custom inventory id
     * @param uuid The player UUID
     * @return The custom inventory key string identifier
     */
    public Optional<String> fromCustomGuiId(int id, UUID uuid) {

        for(Map.Entry<String, CustomInventory> entry : playerInventories.get(uuid).entrySet()) {

            if(entry.getValue().getId() == id) {
                return Optional.of(entry.getKey());
            }
        }

        return Optional.empty();

    }

    /**
     * Check if the custom inventory is mandatory
     * @param keyIdentifier The key identifier of the custom inventory
     * @return If the custom inventory is mandatory
     */
    public boolean isMandatory(String keyIdentifier) {
        return mandatoryInventories.contains(keyIdentifier);
    }

    /**
     * Check if the custom inventory is completed
     * @param keyIdentifier The key identifier of the custom inventory
     * @param uuid The player UUID
     * @return If the custom inventory is completed
     */
    public boolean isCompleted(String keyIdentifier, UUID uuid) {
        return playerInventories
                .get(uuid)
                .get(keyIdentifier)
                .isCompleted(); // Could throw NPE
    }

    @Getter
    @RequiredArgsConstructor
    private enum ConfigField {

        TYPE("type"),
        PATH("path"),
        MANDATORY("mandatory");

        private final String getField;

    }


}
