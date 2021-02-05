package com.leonardobishop.quests.events;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.obj.Messages;
import com.leonardobishop.quests.obj.Options;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class EventPlayerJoin implements Listener {

    private final Quests plugin;

    public EventPlayerJoin(Quests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        plugin.getPlayerManager().loadPlayer(playerUuid);
        if (Options.SOFT_CLEAN_QUESTSPROGRESSFILE_ON_JOIN.getBooleanValue()) {
            plugin.getPlayerManager().getPlayer(playerUuid).getQuestProgressFile().clean();
            if (Options.PUSH_SOFT_CLEAN_TO_DISK.getBooleanValue()) {
                plugin.getPlayerManager().getPlayer(playerUuid).getQuestProgressFile().saveToDisk(false, true);
            }
        }
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
