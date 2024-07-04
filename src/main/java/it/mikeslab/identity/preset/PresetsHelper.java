package it.mikeslab.identity.preset;

import it.mikeslab.commons.api.inventory.util.config.FileUtil;
import it.mikeslab.commons.api.logger.LogUtils;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.inventory.config.GuiConfigRegistrar;
import it.mikeslab.identity.util.ZipUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PresetsHelper {

    public static final String TEMP_FOLDER_NAME = "temp";
    public static final String PRESETS_FOLDER_NAME = "presets";

    private static final String INVENTORIES_SECTION = "guis";

    private static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm";

    private final IdentityPlugin instance;



    public List<String> listLoadablePresents() {

        File presetsFolder = new File(instance.getDataFolder(), PRESETS_FOLDER_NAME);

        if(!presetsFolder.exists()) return new ArrayList<>();

        File[] files = presetsFolder.listFiles();

        if(files == null) return null;

        List<String> fileNames = new ArrayList<>();

        for(File file : files) {
            if(file.isFile() && file.getName().endsWith(".zip")) {
                fileNames.add(file.getName());
            }
        }

        return fileNames;
    }



    public boolean extractPreset(String fileName) {
        File presetsFolder = new File(instance.getDataFolder(), PRESETS_FOLDER_NAME);
        File tempFolder = new File(presetsFolder, TEMP_FOLDER_NAME);

        if(!tempFolder.exists()) {
            tempFolder.mkdirs();
        }

        File zipFile = new File(presetsFolder, fileName);

        if(!zipFile.exists()) return false;

        try {
            ZipUtil.unzip(zipFile, tempFolder);
        } catch (IOException e) {
            LogUtils.warn(
                    LogUtils.LogSource.UTIL,
                    e
            );
        }

        return this.contains(tempFolder, "config.yml");
    }


    public boolean contains(File folder, String fileName) {
        File file = new File(folder, fileName);
        return file.exists();
    }


    public FileConfiguration loadPresetConfig() {
        File presetsFolder = new File(instance.getDataFolder(), PRESETS_FOLDER_NAME);
        File tempFolder = new File(presetsFolder, TEMP_FOLDER_NAME);

        if(!tempFolder.exists()) return null;

        File configFile = new File(tempFolder, "config.yml");

        if(!configFile.exists()) return null;

        return YamlConfiguration.loadConfiguration(configFile);
    }


    public boolean containsInventorySection(FileConfiguration config) {
        if(config.contains(INVENTORIES_SECTION)) {
            return true;
        } else {
            LogUtils.warn(
                    LogUtils.LogSource.CONFIG,
                    "Inventory section not found in the preset configuration"
            );
            return false;
        }
    }


    public boolean checkEntriesValidity(FileConfiguration config) {
        return config.getConfigurationSection(INVENTORIES_SECTION)
                .getKeys(false)
                .stream()
                .allMatch(key -> {
                    boolean isPathPresent = config.contains(INVENTORIES_SECTION + "." + key + "." + GuiConfigRegistrar.ConfigField.PATH.getField());
                    boolean isTypePresent = config.contains(INVENTORIES_SECTION + "." + key + "." + GuiConfigRegistrar.ConfigField.TYPE.getField());

                    return isPathPresent && isTypePresent;
                });

    }


    public void copyToConfig(FileConfiguration config) {

        this.duplicateConfigFile(
                instance.getCustomConfig().getConfiguration()
        );

        instance.getCustomConfig()
                .getConfiguration()
                .set(INVENTORIES_SECTION, config.getConfigurationSection(INVENTORIES_SECTION));

        instance.getCustomConfig()
                .getConfiguration()
                .set(ConfigKey.CHAT_FORMAT.getPath(), config.getString(ConfigKey.CHAT_FORMAT.getPath()));

        instance.getCustomConfig().save();

    }

    public void duplicateConfigFile(FileConfiguration config) {

        String date = new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis());

        File oldConfigFile = new File(instance.getDataFolder(), "config-old-" + date + ".yml");

        try {

            // no need to check if the file exists, it will be overwritten,
            // and it's very odd that it exists since contains a timestamp
            oldConfigFile.createNewFile();

            config.save(oldConfigFile);

        } catch (IOException e) {
            LogUtils.warn(
                    LogUtils.LogSource.CONFIG,
                    e
            );
        }

    }


    public void matchFiles(FileConfiguration config) {

        ConfigurationSection section = config.getConfigurationSection(INVENTORIES_SECTION);

        for(String key : section.getKeys(false)) {

            String path = section.getString(key + "." + GuiConfigRegistrar.ConfigField.PATH.getField());

            if(path == null) continue;

            File file = new File(
                    instance.getDataFolder(),
                    PRESETS_FOLDER_NAME +
                            File.separator + TEMP_FOLDER_NAME +
                            File.separator + path
            );

            if(file.exists()) {
                File destination = new File(instance.getDataFolder(), path);
                try {
                    FileUtil.copyFile(file, destination);
                } catch (IOException e) {
                    LogUtils.warn(
                            LogUtils.LogSource.UTIL,
                            e
                    );
                }
            } else {
                LogUtils.warn(
                        LogUtils.LogSource.CONFIG,
                        "File " + path + " not found in the preset"
                );
            }
        }
    }











}
