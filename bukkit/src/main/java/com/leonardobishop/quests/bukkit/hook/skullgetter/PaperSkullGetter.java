package com.leonardobishop.quests.bukkit.hook.skullgetter;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * Utilises {@link PlayerProfile} and {@link Bukkit#createProfile(UUID, String)} introduced by Paper in 1.12
 */
public class PaperSkullGetter extends SkullGetter {

    public PaperSkullGetter(BukkitQuestsPlugin plugin) {
        super(plugin);
    }

    @Override
    void applyName(SkullMeta meta, String name) {
        PlayerProfile profile = Bukkit.getServer().createProfile(null, name);
        meta.setPlayerProfile(profile);
    }

    @Override
    void applyUniqueId(SkullMeta meta, UUID uniqueId) {
        PlayerProfile profile = Bukkit.getServer().createProfile(uniqueId, null);
        meta.setPlayerProfile(profile);
    }

    @Override
    void applyBase64(SkullMeta meta, String base64) {
        PlayerProfile profile = Bukkit.getServer().createProfile(UUID.randomUUID(), null);
        profile.setProperty(new ProfileProperty("textures", base64));
        meta.setPlayerProfile(profile);
    }
}
