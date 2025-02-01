package com.leonardobishop.quests.bukkit.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SoundUtils {

    private static final Map<String, Sound> soundCache = new HashMap<>();
    private static final Method soundValueOfMethod;

    static {
        try {
            soundValueOfMethod = Sound.class.getDeclaredMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Sound#valueOf method could not be found", e);
        }
    }

    /**
     * Play a sound to a player
     *
     * @param soundString the sound string as formatted by a user in the config, e.g. ENTITY_PLAYER_LEVELUP:2:3
     *                    or namespaced, e.g (minecraft:block.decorated_pot.step):2:3
     */
    public static void playSoundForPlayer(Player player, String soundString) {
        if (soundString == null || soundString.isEmpty()) {
            return;
        }

        String[] parts = soundString.split(":");
        boolean namespaced = parts.length >= 2 && parts[0].startsWith("(") && parts[1].endsWith(")");

        // (namespace:key):pitch:volume
        // 0          1    2     3
        // ENTITY_PLAYER_LEVELUP:2:3
        // 0                     1 2
        int pitchIndex = namespaced ? 2 : 1;
        int volumeIndex = pitchIndex + 1;

        float pitch = 1.0f;
        if (parts.length >= pitchIndex + 1) {
            try {
                pitch = Float.parseFloat(parts[pitchIndex]);
            } catch (NumberFormatException ignored) {
            }
        }

        float volume = 3.0f;
        if (parts.length >= volumeIndex + 1) {
            try {
                volume = Float.parseFloat(parts[volumeIndex]);
            } catch (NumberFormatException ignored) {
            }
        }

        if (namespaced) {
            // 0123456789
            // (space:id)
            // (space - length 6
            // id) - length 3
            String sound = soundString.substring(1, parts[0].length() + parts[1].length());
            player.playSound(player.getLocation(), sound, volume, pitch);
            return;
        }

        Sound sound = soundCache.computeIfAbsent(parts[0], name -> {
            try {
                return (Sound) soundValueOfMethod.invoke(null, parts[0]);
            } catch (IllegalAccessException | InvocationTargetException e) {
                final Throwable cause = e.getCause();

                if (cause instanceof IllegalArgumentException) {
                    return null;
                } else {
                    throw new IllegalStateException("Sound#valueOf invocation failed", e);
                }
            }
        });

        if (sound == null) {
            return;
        }

        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
