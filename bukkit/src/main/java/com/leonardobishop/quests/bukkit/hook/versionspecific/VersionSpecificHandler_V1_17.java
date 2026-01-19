package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Goat;

public class VersionSpecificHandler_V1_17 extends VersionSpecificHandler_V1_16 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_17;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isCaveVinesPlantWithBerries(BlockData blockData) {
        return blockData instanceof CaveVinesPlant caveVinesPlant && caveVinesPlant.isBerries();
    }

    @Override
    public boolean isGoat(Entity entity) {
        return entity instanceof Goat;
    }

    @Override
    public boolean isCake(Material type) {
        return super.isCake(type) || Tag.CANDLE_CAKES.isTagged(type);
    }
}
