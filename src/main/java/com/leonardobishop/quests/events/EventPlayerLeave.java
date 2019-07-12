package com.leonardobishop.quests.events;

import com.leonardobishop.quests.Quests;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class EventPlayerLeave implements Listener {

    private Quests plugin;

    public EventPlayerLeave(Quests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerQuitEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                 plugin.getPlayerManager().getPlayer(playerUuid).getQuestProgressFile().saveToDisk();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getPlayerManager().removePlayer(playerUuid);
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

}
