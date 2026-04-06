package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class VersionSpecificHandler_V26_1_1 extends VersionSpecificHandler_V26_1 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V26_1_1;
    }
}
