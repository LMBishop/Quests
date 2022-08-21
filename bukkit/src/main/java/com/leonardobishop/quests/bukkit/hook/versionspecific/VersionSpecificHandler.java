package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

//TODO move titles, itemgetter, other version specific shite in here
public interface VersionSpecificHandler {

    int getMinecraftVersion();

    boolean isPlayerGliding(Player player);

    boolean isPlayerOnStrider(Player player);

    boolean isOffHandSwap(ClickType clickType);

    boolean isOffHandEmpty(Player player);

    int getAvailableSpace(Player player, ItemStack newItemStack);

    boolean isFurnaceInventoryType(InventoryType type);

    boolean isHotbarMoveAndReaddSupported();
}
