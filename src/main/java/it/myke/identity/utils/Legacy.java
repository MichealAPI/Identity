package it.myke.identity.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.stream.Collectors;

public class Legacy {

    public static String translate(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static List<String> translate(List<Component> component) {
        return component.stream().map(LegacyComponentSerializer.legacySection()::serialize).collect(Collectors.toList());
    }


    public static Component translate(String string) {
        return LegacyComponentSerializer.legacySection().deserialize(string);
    }




    public static String legacyTranslate(String string) {
        return LegacyComponentSerializer.legacySection().serialize(translate(string));
    }



}
