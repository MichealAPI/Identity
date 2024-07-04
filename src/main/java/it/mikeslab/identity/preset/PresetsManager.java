package it.mikeslab.identity.preset;

import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.util.config.FileUtil;
import it.mikeslab.commons.api.logger.LogUtils;
import it.mikeslab.commons.api.various.message.MessageHelperImpl;
import it.mikeslab.commons.api.various.util.XPotion;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.config.lang.LanguageKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@RequiredArgsConstructor
public class PresetsManager {

    private static final String DEFAULT_FOLDER = "inventories";

    private final IdentityPlugin instance;

    public void loadExternalPreset(CommandSender sender, String fileName) {

        PresetsHelper helper = new PresetsHelper(instance);

        MessageHelperImpl messageHelper = instance.getMessageHelper();

        CompletableFuture.runAsync(() -> {

                    List<String> loadablePresets = helper.listLoadablePresents();

                    if (!loadablePresets.contains(fileName)) {
                        messageHelper.sendMessage(
                                sender,
                                LanguageKey.PRESET_DOESNT_EXISTS,
                                Placeholder.unparsed("file", fileName)
                        );
                        return;
                    }

                    if (!helper.extractPreset(fileName)) {
                        messageHelper.sendMessage(sender, LanguageKey.PRESET_EXTRACT_ERROR);
                        return;
                    }

                    FileConfiguration config = helper.loadPresetConfig();

                    if (!helper.containsInventorySection(config)) {
                        messageHelper.sendMessage(sender, LanguageKey.PRESET_NO_INVENTORY_SECTION);
                        return;
                    }

                    if (!helper.checkEntriesValidity(config)) {
                        messageHelper.sendMessage(sender, LanguageKey.PRESET_INVALID_ENTRIES);
                        return;
                    }

                    // Moves the extracted preset configuration section to the
                    // main config
                    helper.copyToConfig(config);

                    helper.matchFiles(config);

                    instance.reload();

                    messageHelper.sendMessage(
                            sender,
                            LanguageKey.PRESET_LOADED,
                            Placeholder.unparsed("file", fileName)
                    );

                    this.sendNotes(sender, config);

                    File presetsFolder = new File(instance.getDataFolder(), PresetsHelper.PRESETS_FOLDER_NAME);
                    File tempFolder = new File(presetsFolder, PresetsHelper.TEMP_FOLDER_NAME);

                    FileUtil.deleteFolderRecursive(tempFolder);
                }
        );
    }

    public void extractDefaults() {

        InputStream defaultFolder = instance.getResource(DEFAULT_FOLDER);

        boolean canExtract = instance
                .getCustomConfig()
                .getBoolean(ConfigKey.EXTRACT_DEFAULTS);

        if(!canExtract) return;

        if(defaultFolder == null) {
            LogUtils.log(
                    Level.WARNING,
                    LogUtils.LogSource.CONFIG,
                    "Default folder not found, cannot extract default presets. Is it a compiled version?"
            );
            return;
        }

        this.createPresetsFolder();

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

    private void createPresetsFolder() {
        File presetsFolder = new File(instance.getDataFolder(), PresetsHelper.PRESETS_FOLDER_NAME);
        if (!presetsFolder.exists())
            presetsFolder.mkdir();
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
            LogUtils.log(
                    Level.SEVERE,
                    LogUtils.LogSource.CONFIG,
                    "An error occurred while saving the configuration file: " + file.getName()
            );
            e.printStackTrace();

        }
    }

    private void sendNotes(CommandSender sender, FileConfiguration presetConfiguration) {

        Component author = ComponentsUtil.getComponent(presetConfiguration,PresetField.AUTHOR.getField());
        Component version = ComponentsUtil.getComponent(presetConfiguration, PresetField.VERSION.getField());
        Component notes = ComponentsUtil.getComponent(presetConfiguration, PresetField.NOTES.getField());

        List<Component> format = ComponentsUtil.getComponentList(Arrays.asList(
                "<dark_gray><strikethrough>------------------------------------------------",
                "<gray>Author<gold>: <white><author>",
                "<gray>Version<gold>: <white><version>",
                "<gray>Notes<gold>: <white><notes>",
                "<dark_gray><strikethrough>------------------------------------------------"
        ), Placeholder.component("author", author), Placeholder.component("version", version), Placeholder.component("notes", notes));

        Audience audience = instance.getAudiences().sender(sender);

        if(format == null) return;

        format.forEach(audience::sendMessage);

    }


    @Getter
    @RequiredArgsConstructor
    private enum PresetField {
        AUTHOR("author"),
        VERSION("version"),
        NOTES("notes");

        private final String field;
    }

}
