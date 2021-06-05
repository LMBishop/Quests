package com.leonardobishop.quests.listener;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.util.Messages;
import com.leonardobishop.quests.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final Quests plugin;

    public PlayerJoinListener(Quests plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        plugin.getPlayerManager().loadPlayer(event.getUniqueId());
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
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getPlayer().sendMessage(plugin.getUpdater().getMessage()), 50L);
        }

        // run a full check to check for any missed quest completions
        plugin.getQuestCompleter().queueFullCheck(plugin.getPlayerManager().getPlayer(playerUuid).getQuestProgressFile());
    }

}
