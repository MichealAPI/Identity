package it.mikeslab.identity.disk;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

@AllArgsConstructor
public class Lang {
    private FileConfiguration config;

    public static String
            MALE_GENDER,
            FEMALE_GENDER,
            NON_BINARY_GENDER;


    public static Component
            ALREADY_HAVE_IDENTITY,
            AGE_SET_OTHER,
            NAME_SET_OTHER,
            GENDER_SET_OTHER,
            IDENTITY_AINT_SET,
            AGE_EDITED,
            GENDER_EDITED,
            NAME_EDITED,
            IDENTITY_RESET_KICKMESSAGE,
            NO_PERMISSION,
            SETUP_COMPLETED,
            PLAYER_NOT_FOUND,
            MAX_AGE_REACHED,
            MIN_AGE_REACHED,
            AGE_CONFIRMED,
            PLAYER_RESET,
            INSERT_NAME,
            INSERT_NAME_TITLE,
            INSERT_NAME_SUBTITLE,
            NAME_CONFIRMED,
            LASTNAME_EXCEEDS_MAX_LENGTH,
            FIRSTNAME_EXCEEDS_MAX_LENGTH,
            NAME_DICTIONARY,
            LASTNAME_REQUIRED,
            GENDER_FEMALE_SELECTED,
            GENDER_MALE_SELECTED,
            GENDER_NON_BINARY_SELECTED,
            GENDER_NOT_VALID,
            NAME_NOT_VALID,
            AGE_DISABLED,
            NAME_DISABLED,
            GENDER_DISABLED,
            NAME_TOO_SHORT;



    public void init() {
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                String fieldName = field.getName().replace("_", "-").toLowerCase();
                if (field.getType() == Component.class) {
                    field.set(field, MiniMessage.miniMessage().deserialize(config.getString(fieldName)));
                }

                if (field.getType() == String.class) {
                    field.set(field, config.getString(fieldName));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();

            }
        }
    }

    private static transient final DecimalFormat MONEY_FORMAT = new DecimalFormat("###,###");

    public static String formatMoney(long money) {
        return "$" + MONEY_FORMAT.format(money);
    }

}
