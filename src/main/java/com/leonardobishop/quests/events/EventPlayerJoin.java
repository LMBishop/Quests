package com.leonardobishop.quests.events;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.obj.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class EventPlayerJoin implements Listener {

    private Quests plugin;

    public EventPlayerJoin(Quests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        plugin.getPlayerManager().loadPlayer(playerUuid);
        if (plugin.getDescription().getVersion().contains("beta") && event.getPlayer().hasPermission("quests.admin")) {
            event.getPlayer().sendMessage(Messages.BETA_REMINDER.getMessage());
        }
        if (plugin.getUpdater().isUpdateReady() && event.getPlayer().hasPermission("quests.admin")) {
            // delay for a bit so they actually see the message
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().sendMessage(plugin.getUpdater().getMessage());
                }
            }.runTaskLater(plugin, 50L);
        }
    }

}
