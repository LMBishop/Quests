package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.Entity;

import java.util.List;

public class VersionSpecificHandler12 extends VersionSpecificHandler11 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 12;
    }

    @Override
    public List<Entity> getPassengers(Entity entity) {
        return entity.getPassengers();
    }
}
