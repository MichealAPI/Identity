package it.mikeslab.identity.utils;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class CustomHeads {



    public static ItemStack getCustomHead(final String val, final Component name, final List<Component> lore) {
        final ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
        if (!val.isEmpty()) {
            final SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            gameProfile.getProperties().put("textures", new Property("textures", translateValue(val)));
            try {
                final Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, gameProfile);
            } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException ex3) {
                ex3.printStackTrace();
            }
            skullMeta.setDisplayName(Legacy.translate(name));
            skullMeta.setLore(Legacy.translate(lore));
            head.setItemMeta(skullMeta);
        }
        return head;
    }




    public static String translateValue(String val) {
        return Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/"+val+"\"}}}").getBytes(StandardCharsets.UTF_8));
    }
}