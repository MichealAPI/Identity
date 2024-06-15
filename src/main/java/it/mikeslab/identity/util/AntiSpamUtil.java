package it.mikeslab.identity.util;

/**
 * Utility class for anti-spam operations
 * Credits to: <a href="https://github.com/mega-wave/AntiSpam/blob/main/src/main/java/megawave/antispam/Utility.java">...</a>
 */
public class AntiSpamUtil {

    public static double calculateSimilarity(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int lenS1 = s1.length();
        int lenS2 = s2.length();

        if (lenS1 == 0 || lenS2 == 0) {
            return 0.0;
        }

        int[][] matrix = new int[lenS1 + 1][lenS2 + 1];
        for (int i = 0; i <= lenS1; i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j <= lenS2; j++) {
            matrix[0][j] = j;
        }
        for (int i = 1; i <= lenS1; i++) {
            for (int j = 1; j <= lenS2; j++) {
                int cost = (s1.charAt(i - 1) != s2.charAt(j - 1)) ? 1 : 0;
                matrix[i][j] = min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1, matrix[i - 1][j - 1] + cost);
            }
        }

        double maxLength = Math.max(lenS1, lenS2);
        double distance = matrix[lenS1][lenS2];

        return (1.0 - distance / maxLength) * 100.0;
    }

    private static int min(int a, int b, int c) {
        if (a < b && a < c) {
            return a;
        } else if (b < c) {
            return b;
        }
        return c;
    }

}
