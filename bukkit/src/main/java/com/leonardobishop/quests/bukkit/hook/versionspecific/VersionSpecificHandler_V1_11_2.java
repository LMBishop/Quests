package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.entity.Entity;

import java.util.List;

public class VersionSpecificHandler_V1_11_2 extends VersionSpecificHandler_V1_11 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_11_2;
    }

    @Override
    public List<Entity> getPassengers(Entity entity) {
        return entity.getPassengers();
    }
}
