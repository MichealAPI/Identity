package it.mikeslab.identity.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class for anti-spam operations
 * Credits to: <a href="https://github.com/mega-wave/AntiSpam/blob/main/src/main/java/megawave/antispam/Utility.java">...</a>
 */
@UtilityClass
public class AntiSpamUtil {

    /**
     * Check if a message is spamming
     * @param m1 the message to check
     * @param m2 the message to compare
     * @return
     */
    public boolean isSpamming(String m1, String m2) {
        return m1.toLowerCase().contains(m2);
    }

}
