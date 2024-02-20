package com.leonardobishop.quests.bukkit.hook.cmi;

import com.Zrips.CMI.CMI;
import org.bukkit.entity.Player;

public class CMIHook implements AbstractCMIHook {

    private final CMI cmi;

    public CMIHook() {
        this.cmi = CMI.getInstance();
    }

    @Override
    public boolean isAfk(Player player) {
        return cmi.getPlayerManager().getUser(player).isAfk();
    }
}
