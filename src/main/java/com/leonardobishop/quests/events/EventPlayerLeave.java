package com.leonardobishop.quests.events;

import com.leonardobishop.quests.Quests;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class EventPlayerLeave implements Listener {

    @EventHandler
    public void onEvent(PlayerQuitEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                 Quests.getPlayerManager().getPlayer(playerUuid).getQuestProgressFile().saveToDisk();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Quests.getPlayerManager().removePlayer(playerUuid);
                    }
                }.runTask(Quests.getInstance());
            }
        }.runTaskAsynchronously(Quests.getInstance());
    }

}
