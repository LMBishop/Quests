package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class VersionSpecificHandler_V1_21_2 extends VersionSpecificHandler_V1_20_4 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_21_2;
    }

    @Override
    public boolean isCraftingControlDropAllSupported() {
        return true;
    }
}
