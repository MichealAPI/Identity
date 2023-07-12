package it.mikeslab.identity.utils;


import java.util.List;
import java.util.stream.Collectors;

public class FormatUtils {


    public static List<String> replaceList(List<String> list, String key, String value) {
        return list.stream().map(s -> s.replace(key, value)).collect(Collectors.toList());
    }


    public static String loreListToSingleString(List<String> lore) {
        String loreFinal = "";
        for(String loreLine : lore) {
            loreFinal = loreLine + "\n";
        }
        return loreFinal;
    }

    public static String firstUppercase(String s) {
        char first = Character.toUpperCase(s.charAt(0));
        return first + s.substring(1);
    }

    public static boolean isAlphanumeric(String s) {
        return s.matches("^[a-zA-Z0-9]*$");
    }

}