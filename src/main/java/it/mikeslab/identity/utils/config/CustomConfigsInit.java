package it.mikeslab.identity.utils.config;

import it.mikeslab.identity.Identity;
import it.mikeslab.identity.obj.Person;
import it.mikeslab.identity.utils.PersonUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class CustomConfigsInit {
    private Identity plugin;
    private HashMap<String, FileConfiguration> configs;


    public void init(Identity plugin) {
        this.plugin = plugin;
        this.configs = new HashMap<>();
    }

    /**
     * If the file doesn't exist, copy it from the jar
     *
     * @param config The name of the file you want to load.
     */
    public YamlConfiguration initializeCustom(String config) {
        try {
            File conf = new File(plugin.getDataFolder(), config);
            if(!conf.exists())
                plugin.saveResource(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        YamlConfiguration configYAML = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), config));
        this.configs.put(config, configYAML);

        return configYAML;
    }

    /**
     * It returns the FileConfiguration of the file named "lang.yml" in the configs HashMap
     *
     * @return The configs HashMap is being returned.
     */
    public FileConfiguration getLangConfig() {
        return configs.get("lang.yml");
    }

    public FileConfiguration getSettingsConfig() {
        return configs.get("settings.yml");
    }


    public FileConfiguration getDataConfig() {
        return configs.get("data.yml");
    }

    public FileConfiguration getInventoriesConfig() {
        return configs.get("inventories.yml");
    }


    public void reloadConfigs() {
        for(String configString : configs.keySet()) {
            initializeCustom(configString);
        }
    }

    public void reload(String configString) {
        saveConfig(configString);
        initializeCustom(configString);
    }


    public void saveConfig(String configName) {
        try {
            configs.get(configName).save(new File(plugin.getDataFolder() + File.separator + configName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saving data into config by Person class (Schema) objects
     * @param uuid Player UUID
     * @param personUtil
     */
    public void saveInConfig(UUID uuid, PersonUtil personUtil) {
        FileConfiguration data = getDataConfig();
        Person person = personUtil.getPerson(uuid);
        if(person.getName() != null) data.set("data." + uuid.toString() + ".name", person.getName());
        if(person.getGender() != null) data.set("data." + uuid.toString() + ".gender", person.getGender());
        if(person.getAge() != -1) data.set("data." + uuid.toString() + ".age", person.getAge());
        reload("data.yml");
    }



}
