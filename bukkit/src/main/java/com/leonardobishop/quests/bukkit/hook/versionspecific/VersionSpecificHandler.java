package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

//TODO move titles, itemgetter, other version specific shite in here
public interface VersionSpecificHandler {

    @SuppressWarnings("unused")
    int getMinecraftVersion();

    boolean isPlayerGliding(Player player);

    boolean isPlayerOnCamel(Player player);

    boolean isPlayerOnDonkey(Player player);

    boolean isPlayerOnHorse(Player player);

    boolean isPlayerOnLlama(Player player);

    boolean isPlayerOnMule(Player player);

    boolean isPlayerOnSkeletonHorse(Player player);

    boolean isPlayerOnStrider(Player player);

    boolean isPlayerOnZombieHorse(Player player);

    boolean isOffHandSwap(ClickType clickType);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isOffHandEmpty(Player player);

    int getAvailableSpace(Player player, ItemStack newItemStack);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isHotbarMoveAndReaddSupported();

    boolean isCaveVinesPlantWithBerries(BlockData blockData);

    ItemStack getItemInMainHand(Player player);

    ItemStack[] getSmithItems(SmithItemEvent event);

    String getSmithMode(SmithItemEvent event);

    boolean isGoat(Entity entity);
}
