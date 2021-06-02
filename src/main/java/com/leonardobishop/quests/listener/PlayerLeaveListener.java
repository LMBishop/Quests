package com.leonardobishop.quests.listener;

import com.leonardobishop.quests.Quests;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final Quests plugin;

    public PlayerLeaveListener(Quests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerQuitEvent event) {
        plugin.getPlayerManager().removePlayer(event.getPlayer().getUniqueId());
    }

}
