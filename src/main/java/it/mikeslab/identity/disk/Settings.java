package it.mikeslab.identity.disk;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Settings {
    private FileConfiguration config;
    public static Boolean
            ONJOIN_MENU_ENABLED,
            AGE_ENABLED,
            GENDER_ENABLED,
            NAME_ENABLED,
            LASTNAME_REQUIRED,
            NAME_TITLEBAR_ENABLED,
            UPDATES_SHOWN,
            ALPHANUMERIC_ONLY;

    public static InventoryType
            INVENTORY_NAME_TYPE,
            INVENTORY_AGE_TYPE;

    public static int
            MENU_SHOW_DELAY,
            MIN_AGE,
            MAX_AGE,
            FIRSTNAME_MIN_LENGTH,
            FIRSTNAME_MAX_LENGTH,
            LASTNAME_MIN_LENGTH,
            LASTNAME_MAX_LENGTH,
            ANVIL_GUI__ITEM__CUSTOM_MODEL_DATA = -1;

    public static String
            ANVIL_GUI__TITLE;

    public static Material
            ANVIL_GUI__ITEM__MATERIAL;

    public static Component
            PLACEHOLDERS__MALE,
            PLACEHOLDERS__FEMALE,
            PLACEHOLDERS__NONBINARY,
            ANVIL_GUI__ITEM__DISPLAY_NAME;

    public static List<Component>
            ANVIL_GUI__ITEM__LORE;

    public void init() {
        for(Field field : getClass().getDeclaredFields()) {
            if(Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                String fieldName = field.getName().replace("__", ".").replace("_", "-").toLowerCase();
                try {
                    if (field.getType() == Boolean.class) {
                        field.set(this, config.getBoolean(fieldName));
                    }

                    if(field.getType().equals(Integer.TYPE)) {
                        field.set(this, config.getInt(fieldName));
                    }

                    if(field.getType() == InventoryType.class) {
                        field.set(this, InventoryType.valueOf(config.getString(fieldName)));
                    }

                    if(field.getType() == String.class) {
                        field.set(this, config.getString(fieldName));
                    }

                    if(field.getType() == Component.class) {
                        field.set(this, MiniMessage.miniMessage().deserialize(config.getString(fieldName)));
                    }

                    if(field.getType() == Material.class) {
                        field.set(this, XMaterial.matchXMaterial(config.getString(fieldName)).get().parseMaterial());
                    }

                    if(field.getType() == List.class) {
                        field.set(this, translate(config.getStringList(fieldName)));
                    }



                } catch (IllegalAccessException | NullPointerException e) {
                    Bukkit.getLogger().severe("Error while loading config at field '" + fieldName + "', check if it exists or reset the config.");
                }
            }
        }
    }

    private static transient final DecimalFormat MONEY_FORMAT = new DecimalFormat("###,###");

    public static String formatMoney(long money) {
        return "$" + MONEY_FORMAT.format(money);
    }

    public static List<Component> translate(List<String> list) {
        List<Component> components = new ArrayList<>();
        for(String element : list) {
            components.add(MiniMessage.miniMessage().deserialize(element).decoration(TextDecoration.ITALIC, false));
        }
        return components;
    }


    public static List<String> translateComponent(List<Component> list) {
        List<String> components = new ArrayList<>();
        for(Component element : list) {
            components.add(LegacyComponentSerializer.legacySection().serialize(element));
        }
        return components;
    }


    public enum InventoryType {
        CHEST,
        ANVIL,
        FURNACE
    }

}
