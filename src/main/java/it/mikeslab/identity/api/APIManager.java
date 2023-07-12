package it.mikeslab.identity.api;

import it.mikeslab.identity.utils.PersonUtil;
import it.mikeslab.identity.utils.config.CustomConfigsInit;
import it.mikeslab.identity.obj.Person;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

@AllArgsConstructor
public class APIManager extends APIMethods {
    private final CustomConfigsInit customConfigsInit;
    private final PersonUtil personUtil;




    /**
     * If the player's data exists, return a new Person object with the player's name, gender, and age.
     *
     * @param playerName The Username of the player you want to get the identity of.
     * @return A Person object.
     */
    @Deprecated
    @Override
    public Person getUserIdentity(String playerName) {
        FileConfiguration data = customConfigsInit.getDataConfig();

        if(!data.isConfigurationSection("data." + Bukkit.getOfflinePlayer(playerName).getUniqueId())) {
            return null;
        }

        ConfigurationSection playerSection = data.getConfigurationSection("data." + Bukkit.getOfflinePlayer(playerName).getUniqueId());
        String name = playerSection.getString("name");
        String gender = playerSection.getString("gender");
        int age = playerSection.getInt("age");

        return new Person(name, gender, age);
    }





    /**
     * If the player's data exists, return a new Person object with the player's name, gender, and age.
     *
     * @param playerUUID The UUID of the player you want to get the identity of.
     * @return A Person object.
     */
    @Override
    public Person getUserIdentity(UUID playerUUID) {
        FileConfiguration data = customConfigsInit.getDataConfig();

        if(!data.isConfigurationSection("data." + playerUUID.toString())) {
            return null;
        }

        ConfigurationSection playerSection = data.getConfigurationSection("data." + playerUUID);
        String name = playerSection.getString("name");
        String gender = playerSection.getString("gender");
        int age = playerSection.getInt("age");

        return new Person(name, gender, age);
    }

    @Override
    public void setGender(UUID playerUUID, String gender) {
        personUtil.setGender(playerUUID, customConfigsInit, gender);
    }

    @Override
    public void setName(UUID playerUUID, String name) {
        personUtil.setName(playerUUID, customConfigsInit, name);
    }

    @Override
    public void setAge(UUID playerUUID, int age) {
        personUtil.setAge(playerUUID, customConfigsInit, age);
    }

    @Deprecated
    @Override
    public void setGender(String name, String gender) {
        personUtil.setGender(Bukkit.getOfflinePlayer(name).getUniqueId(), customConfigsInit, gender);
    }

    @Deprecated
    @Override
    public void setName(String player, String name) {
        personUtil.setName(Bukkit.getOfflinePlayer(player).getUniqueId(), customConfigsInit, name);
    }

    @Deprecated
    @Override
    public void setAge(String name, int age) {
        personUtil.setAge(Bukkit.getOfflinePlayer(name).getUniqueId(), customConfigsInit, age);
    }


}



abstract class APIMethods {

    /**
     * If the player's data exists, return a new Person object with the player's name
     * @param playerName
     * @return
     */
    public abstract Person getUserIdentity(String playerName);

    /**
     * If the player's data exists, return a new Person object with the player's uuid
     * @param playerUUID
     * @return
     */
    @Deprecated
    public abstract Person getUserIdentity(UUID playerUUID);

    /**
     * @param playerUUID
     * @param gender
     */
    public abstract void setGender(UUID playerUUID, String gender);

    /**
     *
     * @param playerUUID
     * @param name
     */
    public abstract void setName(UUID playerUUID, String name);

    /**
     * Set the age of a player.
     *
     * @param playerUUID The UUID of the player you want to set the age of.
     * @param age The age you want to set.
     */
    public abstract void setAge(UUID playerUUID, int age);

    /**
     * @deprecated Use {@link #setGender(UUID, String)} instead.
     */
    @Deprecated
    public abstract void setGender(String name, String gender);

    /**
     * @param player The Username of the player you want to set the name of.
     * @param name The name you want to set.
     */
    @Deprecated
    public abstract void setName(String player, String name);

    /**
     * @param name The name of the player you want to set the age of.
     * @param age The age you want to set the player to.
     */
    @Deprecated
    public abstract void setAge(String name, int age);
}

