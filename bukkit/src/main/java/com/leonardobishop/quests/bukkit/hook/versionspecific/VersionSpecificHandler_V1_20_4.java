package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.Nullable;

public class VersionSpecificHandler_V1_20_4 extends VersionSpecificHandler_V1_20 {

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_20_4;
    }

    @Override
    public @Nullable Player getDamager(@Nullable EntityDamageEvent event) {
        if (event == null) {
            return null;
        }

        DamageSource source = event.getDamageSource();
        Entity causingEntity = source.getCausingEntity();

        if (causingEntity instanceof Player) {
            return (Player) causingEntity;
        }

        return null;
    }

    @Override
    public @Nullable Entity getDirectSource(@Nullable EntityDamageEvent event) {
        if (event == null) {
            return null;
        }

        DamageSource source = event.getDamageSource();
        return source.getDirectEntity();
    }
}
