package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVinesPlant;

public class VersionSpecificHandler17 extends VersionSpecificHandler16 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 17;
    }

    @Override
    public boolean isCaveVinesPlantWithBerries(BlockData blockData) {
        return blockData instanceof CaveVinesPlant caveVinesPlant && caveVinesPlant.isBerries();
    }
}
