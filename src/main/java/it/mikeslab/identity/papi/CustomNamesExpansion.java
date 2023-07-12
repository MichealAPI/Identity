package it.mikeslab.identity.papi;

import it.mikeslab.identity.Identity;
import it.mikeslab.identity.disk.Lang;
import it.mikeslab.identity.disk.Settings;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class CustomNamesExpansion extends PlaceholderExpansion {
    private final Identity plugin;
    private FileConfiguration data;

    public CustomNamesExpansion(final Identity identity) {
        this.plugin = identity;
        this.data = identity.getCustomConfigsInit().getDataConfig();
    }


    @Override
    public @NotNull String getIdentifier() {
        return "identity";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MikesLab";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.3.7";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }


    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        data = plugin.getCustomConfigsInit().getDataConfig();
        if (player != null && data.getConfigurationSection("data") != null && data.isConfigurationSection("data." + player.getUniqueId())) {
            switch (params) {
                case "name":
                    String name = data.getString("data." + player.getUniqueId() + ".name") + "";
                    if(name.contains(" ") && name.split(" ")[0].length() > 0) {
                        return name.split(" ")[0];
                    } else if(name.length() > 0)
                        return name;

                case "surname":
                    String surname = data.getString("data." + player.getUniqueId() + ".name") + "";
                    if(surname.contains(" ") && surname.split(" ").length > 1 && surname.split(" ")[1].length() > 0) {
                        return surname.split(" ")[1];
                    }
                case "fullname":
                    String fullName = data.getString("data." + player.getUniqueId() + ".name") + "";

                    if(!fullName.equals("")) {
                        return fullName;
                    }
                case "gender":
                    String gender = (data.getString("data." + player.getUniqueId() + ".gender") + "").toLowerCase(Locale.ROOT);
                    if(gender.equals(Lang.NON_BINARY_GENDER)) {
                        return LegacyComponentSerializer.legacySection().serialize(Settings.PLACEHOLDERS__NONBINARY);
                    }

                    if(gender.equals(Lang.MALE_GENDER)) {
                        return LegacyComponentSerializer.legacySection().serialize(Settings.PLACEHOLDERS__MALE);
                    }

                    if(gender.equals(Lang.FEMALE_GENDER)) {
                        return LegacyComponentSerializer.legacySection().serialize(Settings.PLACEHOLDERS__FEMALE);
                    }
                case "age":
                    int age = data.getInt("data." + player.getUniqueId() + ".age");
                    if(age != 0)
                        return String.valueOf(age);
            }
        }
        return "";
    }
}
