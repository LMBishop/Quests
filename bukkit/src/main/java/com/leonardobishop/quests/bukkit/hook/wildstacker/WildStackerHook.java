package com.leonardobishop.quests.bukkit.hook.wildstacker;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import org.bukkit.entity.LivingEntity;

public class WildStackerHook implements AbstractWildStackerHook {

    @Override
    public int getEntityAmount(LivingEntity entity) {
        return WildStackerAPI.getEntityAmount(entity);
    }
}
