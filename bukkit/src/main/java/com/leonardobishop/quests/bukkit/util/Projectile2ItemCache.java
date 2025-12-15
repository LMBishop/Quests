package com.leonardobishop.quests.bukkit.util;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Provides a cache that links projectiles to the item used to fire them.
 *
 * <p>This cache exists because damage-related events do not expose
 * information about the item from which a projectile was fired.
 * By capturing this association at the time the projectile is created,
 * the item can later be retrieved when handling damage events.</p>
 */
@NullMarked
public final class Projectile2ItemCache implements Listener {

    private final Map<Entity, @Nullable ItemStack> backingMap;

    public Projectile2ItemCache() {
        this.backingMap = WeakHashMap.newWeakHashMap(1024);
    }

    public void registerEvents(final BukkitQuestsPlugin plugin) {
        final PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(this, plugin);

        try {
            Class.forName("com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent");
            pluginManager.registerEvents(new PlayerLaunchProjectileListener(), plugin);
        } catch (final ClassNotFoundException e) {
            // not supported on Spigot
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShootBow(final EntityShootBowEvent event) {
        final LivingEntity shooter = event.getEntity();
        final Entity projectile = event.getProjectile();
        final ItemStack bow = event.getBow();

        // Currently there are no advantages of caching projectiles for non-player arrows.
        // It would be needed to cache these if we needed a task to take damage from mobs.
        if (shooter instanceof Player) {
            this.backingMap.put(projectile, bow);
        }
    }

    public final class PlayerLaunchProjectileListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerLaunchProjectile(final PlayerLaunchProjectileEvent event) {
            final Projectile projectile = event.getProjectile();
            final ItemStack item = event.getItemStack();

            // TODO: doesn't really work for tridents
            // https://github.com/LMBishop/Quests/pull/833
            Projectile2ItemCache.this.backingMap.put(projectile, item);
        }
    }

    public @Nullable ItemStack getItem(final Entity projectile) {
        return this.backingMap.get(projectile);
    }
}
