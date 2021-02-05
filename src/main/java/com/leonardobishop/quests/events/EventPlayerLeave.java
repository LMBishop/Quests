package com.leonardobishop.quests.events;

import com.leonardobishop.quests.Quests;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class EventPlayerLeave implements Listener {

    private final Quests plugin;

    public EventPlayerLeave(Quests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerQuitEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        plugin.getPlayerManager().removePlayer(playerUuid);
    }

}
