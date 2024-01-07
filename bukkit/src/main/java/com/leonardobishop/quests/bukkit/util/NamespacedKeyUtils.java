package com.leonardobishop.quests.bukkit.util;

import org.bukkit.NamespacedKey;

/**
 * Utility class (for compatibility reasons) with method to get {@link NamespacedKey} from string.
 * Contains validation methods from the <a href="https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/NamespacedKey.java">
 * original Spigot server implementation</a> introduced in 1.16.5.
 *
 * @see NamespacedKey#fromString(String)
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class NamespacedKeyUtils {

    public static NamespacedKey fromString(String string) {
        if (string == null) {
            return null;
        }

        String[] parts = string.split(":", 3);
        return switch (parts.length) {
            case 1 -> isValidKey(parts[0]) ? NamespacedKey.minecraft(parts[0]) : null;
            case 2 -> isValidNamespace(parts[0]) && isValidKey(parts[1]) ? new NamespacedKey(parts[0], parts[1]) : null;
            default -> null;
        };
    }

    private static boolean isValidNamespace(String namespace) {
        int len = namespace.length();
        if (len == 0) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            char c = namespace.charAt(i);

            if (!isValidNamespaceChar(c)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidNamespaceChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.' || c == '_' || c == '-';
    }

    private static boolean isValidKey(String key) {
        int len = key.length();
        if (len == 0) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            char c = key.charAt(i);

            if (!isValidKeyChar(c)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidKeyChar(char c) {
        return isValidNamespaceChar(c) || c == '/';
    }
}
