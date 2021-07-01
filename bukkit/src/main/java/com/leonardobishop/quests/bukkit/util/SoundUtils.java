package com.leonardobishop.quests.bukkit.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class SoundUtils {

    /**
     * Play a sound to a player
     *
     * @param soundString the sound string as formatted by a user in the config, e.g ENTITY_PLAYER_LEVELUP:2:3
     */
    public static void playSoundForPlayer(Player player, String soundString) {
        if (soundString == null || soundString.isEmpty()) return;

        String[] parts = soundString.split(Pattern.quote(":"));
        float pitch = 1;
        float volume = 3;
        try {
            switch (parts.length) {
                case 3:
                    volume = Float.parseFloat(parts[2]);
                case 2:
                    pitch = Float.parseFloat(parts[1]);
            }
        } catch (NumberFormatException ignored) { }

        Sound sound;
        try {
            sound = Sound.valueOf(parts[0]);
        } catch (IllegalArgumentException ex) {
            return;
        }

        player.playSound(player.getLocation(), sound, volume, pitch);
    }

}
