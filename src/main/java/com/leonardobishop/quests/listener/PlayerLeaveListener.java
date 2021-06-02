package com.leonardobishop.quests.listener;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import org.bukkit.Bukkit;
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
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) return;
        QuestProgressFile clonedProgressFile = new QuestProgressFile(qPlayer.getQuestProgressFile());
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getPlayerManager().removePlayer(qPlayer.getPlayerUUID(), clonedProgressFile));
    }

}
