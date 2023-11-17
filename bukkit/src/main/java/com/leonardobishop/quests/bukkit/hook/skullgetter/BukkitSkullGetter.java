package com.leonardobishop.quests.bukkit.hook.skullgetter;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Utilises reflection to set {@link SkullMeta} {@code profile} field value using a dedicated method.
 */
@SuppressWarnings("deprecation")
public class BukkitSkullGetter extends SkullGetter {

    private static Method setProfileMethod; // introduced by Bukkit in 1.15.1

    public BukkitSkullGetter(BukkitQuestsPlugin plugin) {
        super(plugin);
    }

    @Override
    void applyName(SkullMeta meta, String name) {
        meta.setOwner(name);
    }

    @Override
    void applyUniqueId(SkullMeta meta, UUID uniqueId) {
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uniqueId));
    }

    @Override
    void applyBase64(SkullMeta meta, String base64) {
        if (setProfileMethod == null) {
            try {
                setProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                setProfileMethod.setAccessible(true);
            } catch (NoSuchMethodException ignored) {
                return;
            }
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            setProfileMethod.invoke(meta, profile);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
    }
}
