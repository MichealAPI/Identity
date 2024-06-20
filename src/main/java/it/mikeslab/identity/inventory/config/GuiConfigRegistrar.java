package it.mikeslab.identity.inventory.config;

import it.mikeslab.commons.api.logger.LoggerUtil;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.CustomInventory;
import it.mikeslab.identity.inventory.InventoryType;
import it.mikeslab.identity.inventory.pojo.ValueMenuContext;
import it.mikeslab.identity.inventory.impl.InputMenu;
import it.mikeslab.identity.inventory.impl.MainMenu;
import it.mikeslab.identity.inventory.impl.SelectorMenu;
import it.mikeslab.identity.inventory.impl.ValueMenu;
import it.mikeslab.identity.inventory.pojo.InventorySettings;
import it.mikeslab.identity.util.InventoryMap;
import it.mikeslab.identity.util.inventory.input.InputMenuLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.nio.file.Path;
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
    private final Map<String, String> mandatoryInventories = new HashMap<>(); // keyId in pair with displayName
                                                                              // which could be defaulted to keyId in case of null
    @Getter
    private double min, max, baseValue;

    @Getter
    private final InputMenuLoader inputMenuLoader;


    public GuiConfigRegistrar(IdentityPlugin instance, ConfigurationSection section) {
        this.instance = instance;
        this.section = section;

        this.playerInventories = new InventoryMap(instance);

        this.inputMenuLoader = new InputMenuLoader(instance);
    }

    public Map<String, CustomInventory> getGuis() {

        Map<String, CustomInventory> guis = new HashMap<>();

        for(Map.Entry<String, GuiRegistrarCache> entry : cache.entrySet()) {

            String keyId = entry.getKey();
            GuiRegistrarCache cache = entry.getValue();

            guis.put(keyId, createInventory(keyId, cache));

        }

        return guis;
    }


    /**
     * Register the configuration for each custom inventory
     */
    public void register() {
        for(String key : section.getKeys(false)) {
            ConfigurationSection configSection = section.getConfigurationSection(key);
            if (!validateConfigSection(key, configSection)) {
                continue;
            }

            String typeAsString = configSection.getString(ConfigField.TYPE.getField());
            InventoryType type = InventoryType.fromString(typeAsString);
            String displayName = configSection.getString(ConfigField.DISPLAY_NAME.getField(), key); // display name defaults to key
            String path = configSection.getString(ConfigField.PATH.getField());

            boolean mandatory = configSection.getBoolean(ConfigField.MANDATORY.getField(), false); // mandatory is defaulted to false

            if (type == InventoryType.VALUE) {
                processValueType(configSection);
            }

            if (type == InventoryType.MAIN) {
                processMainType(key);
            }

            this.cache.put(key, new GuiRegistrarCache(type, path, configSection));

            if(mandatory) {
                addMandatoryInventory(key, displayName);
            }
        }

        if(fallbackGuiIdentifier == null) {
            LoggerUtil.log(
                    IdentityPlugin.PLUGIN_NAME,
                    Level.SEVERE,
                    LoggerUtil.LogSource.CONFIG,
                    "No main menu found in the configuration"
            );
            Bukkit.getPluginManager().disablePlugin(instance);
        }
    }

    /**
     * Validate the configuration section
     * @param key The key of the configuration section
     * @param configSection The configuration section
     * @return Whether the configuration section is valid
     */
    private boolean validateConfigSection(String key, ConfigurationSection configSection) {
        String typeAsString = configSection.getString(ConfigField.TYPE.getField());
        InventoryType type = InventoryType.fromString(typeAsString);
        String path = configSection.getString(ConfigField.PATH.getField());

        if(typeAsString == null) {
            logMissingRequiredField(key);
            return false;
        }

        if(type == null) {
            logMissingRequiredField(key);
            return false;
        }

        if(path == null) {
            logMissingRequiredField(key);
            return false;
        }


        boolean mandatory = configSection.getBoolean(ConfigField.MANDATORY.getField(), false); // mandatory is defaulted to false

        if(mandatory && type == InventoryType.MAIN) {
            LoggerUtil.log(
                    IdentityPlugin.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.CONFIG,
                    "Inventory '" + key + "' is marked as mandatory but is a main menu"
            );
            return false;
        }

        return true;
    }

    private void logMissingRequiredField(String inventoryKeyId) {
        LoggerUtil.log(
                IdentityPlugin.PLUGIN_NAME,
                Level.WARNING,
                LoggerUtil.LogSource.CONFIG,
                "Inventory '" + inventoryKeyId + "' is missing a required field (Required fields:"
                        + ConfigField.TYPE.getField() + ", " + ConfigField.PATH.getField() + ")"
        );
    }

    /**
     * Process the 'value' type configuration section
     * @param configSection The configuration section
     */
    private void processValueType(ConfigurationSection configSection) {
        min = configSection.getDouble(ConfigField.MIN.getField(), Double.MIN_VALUE);
        max = configSection.getDouble(ConfigField.MAX.getField(), Double.MAX_VALUE);
        baseValue = configSection.getDouble(ConfigField.BASE.getField(), 0);
    }

    /**
     * Process the 'main' type configuration section
     * @param key The key of the configuration section
     */
    private void processMainType(String key) {
        this.fallbackGuiIdentifier = key;
    }

    /**
     * Add a mandatory inventory to the registrar
     * @param key The key of the inventory
     * @param displayName The display name of the inventory
     */
    private void addMandatoryInventory(String key, String displayName) {
        mandatoryInventories.put(key, displayName);
    }


    private CustomInventory createInventory(String keyId, GuiRegistrarCache cache) {

        // Replace the path with the correct separator
        String path = cache.getPath().replace("\\", File.separator) // todo should I remove this?
                .replace("/", File.separator);

        // Get the file
        File configFile = new File(instance.getDataFolder(), path); // todo add default sub-path /inventories

        // Check if the file exists
        if(!configFile.exists()) {
            LoggerUtil
                    .log(
                            IdentityPlugin.PLUGIN_NAME,
                            Level.WARNING,
                            LoggerUtil.LogSource.CONFIG,
                            "File at '" + configFile.getAbsolutePath() + "' does not exist"
                    );
            return null;
        }

        Path fullPath = configFile.toPath();
        Path rootPath = instance.getDataFolder().toPath();

        // Get the relative path
        Path relativePath = rootPath.relativize(fullPath);

        // Create the gui config
        InventorySettings settings = new InventorySettings(
                keyId,
                relativePath,
                false, // todo configurable
                cache.getInventoryType()
        );

        switch (cache.getInventoryType()) {

            case SELECTOR: return new SelectorMenu(instance, settings);
            case MAIN: return new MainMenu(instance, settings);
            case VALUE: return new ValueMenu(instance, settings, new ValueMenuContext(baseValue, max, min));
            case INPUT: return new InputMenu(instance, settings);

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

    @Getter
    @RequiredArgsConstructor
    private enum ConfigField {

        TYPE("type"),
        PATH("path"),
        MIN("min"),
        MAX("max"),
        BASE("base"),
        MANDATORY("mandatory"),
        DISPLAY_NAME("displayName");

        private final String field;

    }


}
