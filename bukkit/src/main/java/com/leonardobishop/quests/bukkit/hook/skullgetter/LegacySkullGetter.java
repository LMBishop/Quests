package com.leonardobishop.quests.bukkit.hook.skullgetter;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Utilises reflection to set {@link SkullMeta} {@code profile} field value.
 */
public class LegacySkullGetter extends SkullGetter {

    private static Field profileField;
    private static Method setOwningPlayerMethod; // introduced in 1.12.1

    static {
        try {
            setOwningPlayerMethod = SkullMeta.class.getDeclaredMethod("setOwningPlayer", OfflinePlayer.class);
        } catch (NoSuchMethodException ignored) {
        }
    }

    public LegacySkullGetter(BukkitQuestsPlugin plugin) {
        super(plugin);
    }

    @SuppressWarnings("deprecation")
    @Override
    void applyName(SkullMeta meta, String name) {
        meta.setOwner(name);
    }

    @Override
    void applyUniqueId(SkullMeta meta, UUID uniqueId) {
        if (setOwningPlayerMethod == null) {
            return;
        }

        try {
            setOwningPlayerMethod.invoke(meta, Bukkit.getOfflinePlayer(uniqueId));
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
    }

    @Override
    void applyBase64(SkullMeta meta, String base64) {
        if (profileField == null) {
            try {
                profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                return;
            }
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            profileField.set(meta, profile);
        } catch (IllegalAccessException ignored) {
        }
    }
}
