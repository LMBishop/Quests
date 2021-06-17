package com.leonardobishop.quests.bukkit.listener;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final BukkitQuestsPlugin plugin;

    public PlayerJoinListener(BukkitQuestsPlugin plugin) {
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
            String updateMessage = Messages.QUEST_UPDATER.getMessage()
                    .replace("{newver}", plugin.getUpdater().getReturnedVersion())
                    .replace("{oldver}", plugin.getUpdater().getInstalledVersion())
                    .replace("{link}", plugin.getUpdater().getUpdateLink());
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getPlayer().sendMessage(updateMessage), 50L);
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(playerUuid);
        if (qPlayer == null) return;

        // run a full check to check for any missed quest completions
        plugin.getQuestCompleter().queueFullCheck(qPlayer.getQuestProgressFile());
    }

}
