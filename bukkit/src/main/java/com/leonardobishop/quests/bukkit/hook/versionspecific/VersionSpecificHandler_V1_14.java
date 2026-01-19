package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.block.Biome;

public class VersionSpecificHandler_V1_14 extends VersionSpecificHandler_V1_11_2 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_14;
    }

    @Override
    public String getBiomeKey(Biome biome) {
        return biome.getKey().toString();
    }
}
