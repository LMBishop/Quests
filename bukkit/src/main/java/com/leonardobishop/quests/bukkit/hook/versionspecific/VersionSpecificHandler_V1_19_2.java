package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;

public class VersionSpecificHandler_V1_19_2 extends VersionSpecificHandler_V1_17 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_19_2;
    }

    @Override
    public ItemStack getItem(PlayerBucketEmptyEvent event) {
        return this.getItemInEquipmentSlot(event.getPlayer().getInventory(), event.getHand());
    }
}
