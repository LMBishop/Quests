package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.bukkit.util.CompatUtils;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jspecify.annotations.Nullable;

public class VersionSpecificHandler20 extends VersionSpecificHandler17 implements VersionSpecificHandler {

    private static final boolean DAMAGE_SOURCE_API = CompatUtils.classExists("org.bukkit.damage.DamageSource");

    @Override
    public int getMinecraftVersion() {
        return 20;
    }

    @Override
    public boolean isPlayerOnCamel(Player player) {
        return player.getVehicle() instanceof Camel;
    }

    @Override
    public ItemStack getItem(PlayerBucketEmptyEvent event) {
        return event.getPlayer().getInventory().getItem(event.getHand());
    }

    @Override
    public ItemStack[] getSmithItems(SmithItemEvent event) {
        return new ItemStack[]{
                event.getInventory().getInputEquipment(),
                event.getInventory().getInputMineral(),
                event.getInventory().getInputTemplate()
        };
    }

    @Override
    public String getSmithMode(SmithItemEvent event) {
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe instanceof SmithingTransformRecipe) {
            return "transform";
        } else if (recipe instanceof SmithingTrimRecipe) {
            return "trim";
        } else {
            return null;
        }
    }

    @Override
    public @Nullable Player getDamager(@Nullable EntityDamageEvent event) {
        if (!DAMAGE_SOURCE_API) {
            return super.getDamager(event);
        }

        if (event == null) {
            return null;
        }

        DamageSource source = event.getDamageSource();
        Entity causingEntity = source.getCausingEntity();

        if (causingEntity instanceof Player) {
            return (Player) causingEntity;
        }

        return null;
    }

    @Override
    public @Nullable Entity getDirectSource(@Nullable EntityDamageEvent event) {
        if (!DAMAGE_SOURCE_API) {
            return super.getDamager(event);
        }

        if (event == null) {
            return null;
        }

        DamageSource source = event.getDamageSource();
        return source.getDirectEntity();
    }
}
