package it.mikeslab.identity.utils;

import it.mikeslab.identity.inventories.Inventories;
import it.mikeslab.identity.inventories.InventoryManager;
import it.mikeslab.identity.utils.config.CustomConfigsInit;
import it.mikeslab.identity.utils.postprocess.PostProcessCommands;
import it.mikeslab.identity.Identity;
import it.mikeslab.identity.disk.Lang;
import it.mikeslab.identity.obj.Person;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PersonUtil {
    private final Map<UUID, Person> personHashMap = new HashMap<>();


    // It's a simple Map that stores the player's data.
    public void addPerson(UUID uuid, int age, String name, String gender) {
        personHashMap.put(uuid, new Person(name, gender, age));
    }

    public void removePerson(UUID uuid) {
        personHashMap.remove(uuid);
    }

    public boolean isPerson(UUID uuid) {
        return getPerson(uuid) != null;
    }



    public Person getPerson(UUID uuid) {
        return personHashMap.get(uuid);
    }

    public void setAge(UUID uuid, CustomConfigsInit customConfigsInit, int age) {
        customConfigsInit.getDataConfig().set("data." + uuid.toString() + ".age", age);
        try {
            customConfigsInit.saveConfig("data.yml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setName(UUID uuid, CustomConfigsInit customConfigsInit, String name) {
        customConfigsInit.getDataConfig().set("data." + uuid.toString() + ".name", name);
        try {
            customConfigsInit.saveConfig("data.yml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGender(UUID uuid, CustomConfigsInit customConfigsInit, String gender) {
        customConfigsInit.getDataConfig().set("data." + uuid.toString() + ".gender", gender);
        try {
            customConfigsInit.saveConfig("data.yml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean setup(Player player, CustomConfigsInit customConfigsInit, Identity main, PostProcessCommands postProcessCommands, Inventories inventoryUtils, boolean cmd) {
        FileConfiguration data = customConfigsInit.getDataConfig();
        if(!data.isConfigurationSection("data." + player.getUniqueId())) {
            this.addPerson(player.getUniqueId(), -1, null, null);
            new InventoryManager().openNextInventory(player, main, this, inventoryUtils, postProcessCommands, customConfigsInit, true);
            return true;
        } else if(cmd) {
            player.sendMessage(Legacy.translate(Lang.ALREADY_HAVE_IDENTITY));
            return false;
        }
        return false;
    }





}
