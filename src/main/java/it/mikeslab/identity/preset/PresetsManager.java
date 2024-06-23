package it.mikeslab.identity.preset;

import it.mikeslab.commons.api.logger.LoggerUtil;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

@RequiredArgsConstructor
public class PresetsManager {

    private static final String DEFAULT_FOLDER = "inventories";

    private final IdentityPlugin instance;

    public void loadPresets() {

        InputStream defaultFolder = instance.getResource(DEFAULT_FOLDER);

        boolean canExtract = instance
                .getCustomConfig()
                .getBoolean(ConfigKey.EXTRACT_DEFAULTS);

        if(!canExtract) return;

        if(defaultFolder == null) {
            LoggerUtil.log(
                    Level.WARNING,
                    LoggerUtil.LogSource.CONFIG,
                    "Default folder not found, cannot extract default presets. Is it a compiled version?"
            );
            return;
        }

        String subFolder = DEFAULT_FOLDER + File.separator;

        extractConfiguration(subFolder + "input.yml");
        extractConfiguration(subFolder + "value.yml");
        extractConfiguration(subFolder + "selector.yml");
        extractConfiguration(subFolder + "main.yml");

        ConfigurationSection settingSection = instance
                .getCustomConfig()
                .getConfiguration()
                .getConfigurationSection("settings");

        settingSection.set("extract-defaults", false);

        this.saveConfig(
                instance.getCustomConfig().getFile(),
                instance.getCustomConfig().getConfiguration()
        );

    }


    private File extractConfiguration(String fileName) {
        File file = new File(instance.getDataFolder(), fileName);
        if (!file.exists())
            instance.saveResource(fileName, false);
        return file;
    }


    private void saveConfig(File file, FileConfiguration config) { // a custom saver
        // prevents comments from being removed
        try {
            config.save(file);
        } catch (IOException e) {
            LoggerUtil.log(
                    Level.SEVERE,
                    LoggerUtil.LogSource.CONFIG,
                    "An error occurred while saving the configuration file: " + file.getName()
            );
            e.printStackTrace();

        }
    }


}
