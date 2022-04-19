package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.event.inventory.InventoryType;

public class VersionSpecificHandler14 extends VersionSpecificHandler9 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 14;
    }

    @Override
    public boolean isFurnaceInventoryType(InventoryType type) {
        return type == InventoryType.BLAST_FURNACE || type == InventoryType.FURNACE || type == InventoryType.SMOKER;
    }
}
