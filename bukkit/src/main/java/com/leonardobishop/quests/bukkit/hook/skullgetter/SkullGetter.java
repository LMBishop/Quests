package com.leonardobishop.quests.bukkit.hook.skullgetter;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public abstract class SkullGetter {

    protected final BukkitQuestsPlugin plugin;

    public SkullGetter(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public final boolean apply(SkullMeta meta, String name, String uniqueIdString, String base64) {
        if (name != null) {
            applyName(meta, name);
            return true;
        }

        if (uniqueIdString != null) {
            UUID uniqueId;
            try {
                uniqueId = UUID.fromString(uniqueIdString);
            } catch (IllegalArgumentException e) {
                uniqueId = null;
            }

            if (uniqueId != null) {
                applyUniqueId(meta, uniqueId);
                return true;
            }
        }

        if (base64 != null) {
            applyBase64(meta, base64);
            return true;
        }

        return false;
    }

    abstract void applyName(SkullMeta meta, String name);

    abstract void applyUniqueId(SkullMeta meta, UUID uniqueId);

    abstract void applyBase64(SkullMeta meta, String base64);
}
