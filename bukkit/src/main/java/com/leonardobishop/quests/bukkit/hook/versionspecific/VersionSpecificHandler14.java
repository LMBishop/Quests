package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.block.Biome;

public class VersionSpecificHandler14 extends VersionSpecificHandler12 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 14;
    }

    @Override
    public String getBiomeKey(Biome biome) {
        return biome.getKey().toString();
    }
}
