package com.leonardobishop.quests.bukkit.listener;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final BukkitQuestsPlugin plugin;

    public PlayerJoinListener(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        if (plugin.getDescription().getVersion().contains("beta") && event.getPlayer().hasPermission("quests.admin")) {
            event.getPlayer().sendMessage(Messages.BETA_REMINDER.getMessage());
        }

        final Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if (!player.isOnline()) return;
            plugin.getPlayerManager().loadPlayer(player.getUniqueId());
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                if (qPlayer == null) return;
                // run a full check to check for any missed quest completions
                plugin.getQuestCompleter().queueFullCheck(qPlayer.getQuestProgressFile());
            });
        }, plugin.getQuestsConfig().getInt("options.storage.synchronisation.delay-loading", 0));
    }

}
