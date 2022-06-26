package com.leonardobishop.quests.bukkit.listener;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final BukkitQuestsPlugin plugin;

    public PlayerLeaveListener(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerQuitEvent event) {
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) return;
        Player player = event.getPlayer();
        plugin.getQuestsLogger().debug("PlayerLeaveListener: " + player.getUniqueId() + " (" + player.getName() + ")");
        plugin.getPlayerManager().removePlayer(qPlayer.getPlayerUUID());
    }

}
