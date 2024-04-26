package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.Camel;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;

public class VersionSpecificHandler20 extends VersionSpecificHandler17 implements VersionSpecificHandler {

    @Override
    public boolean isPlayerOnCamel(Player player) {
        return player.getVehicle() instanceof Camel;
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
}
