package it.mikeslab.identity.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {


    public String capitalize(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }


}
