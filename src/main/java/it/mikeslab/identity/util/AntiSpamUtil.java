package it.mikeslab.identity.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class for anti-spam operations
 * Credits to: <a href="https://github.com/mega-wave/AntiSpam/blob/main/src/main/java/megawave/antispam/Utility.java">...</a>
 */
@UtilityClass
public class AntiSpamUtil {

    public boolean isSpamming(String message, String lastMessage) {
        return message.toLowerCase().contains(lastMessage);
    }

}
