package com.leonardobishop.quests.bukkit.hook.essentials;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EssentialsHook implements AbstractEssentialsHook {

    private final Essentials ess;

    public EssentialsHook() {
        this.ess = ((Essentials) Bukkit.getPluginManager().getPlugin("Essentials"));
    }

    @Override
    public boolean isAfk(Player player) {
        return ess.getUser(player).isAfk();
    }
}
