package com.leonardobishop.quests.bukkit.hook.skullgetter;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.UUID;

/**
 * Utilises {@link PlayerProfile} and {@link Bukkit#createPlayerProfile(UUID, String)} introduced by Bukkit in 1.18.1
 */
@SuppressWarnings("deprecation")
public class ModernSkullGetter extends BukkitSkullGetter {

    public ModernSkullGetter(BukkitQuestsPlugin plugin) {
        super(plugin);
    }

    @Override
    void applyName(SkullMeta meta, String name) {
        PlayerProfile profile = Bukkit.createPlayerProfile(null, name);
        meta.setOwnerProfile(profile);
    }

    @Override
    void applyUniqueId(SkullMeta meta, UUID uniqueId) {
        PlayerProfile profile = Bukkit.createPlayerProfile(uniqueId, null);
        meta.setOwnerProfile(profile);
    }
}
