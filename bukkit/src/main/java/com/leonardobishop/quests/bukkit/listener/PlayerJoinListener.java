package com.leonardobishop.quests.bukkit.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.enums.PluginMessagingChannels;
import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final BukkitQuestsPlugin plugin;

    public PlayerJoinListener(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
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

        Player player = event.getPlayer();

//        Bukkit.getScheduler().runTaskLater(plugin, () -> {
//            if (!player.isOnline()) return;
//
//            ByteArrayDataOutput out = ByteStreams.newDataOutput();
//            out.writeUTF("acquireLock");
//            out.writeUTF(player.getUniqueId().toString());
//            player.sendPluginMessage(plugin, PluginMessagingChannels.QUESTS_LOCKS_CHANNEL, out.toByteArray());
//        }, 20L);


        plugin.getScheduler().doAsync(() -> {
            plugin.getPlayerManager().loadPlayer(player.getUniqueId());

            plugin.getScheduler().doSync(() -> {
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
                if (qPlayer == null) return;

                // run a full check to check for any missed quest completions
                plugin.getQuestCompleter().queueFullCheck(qPlayer.getQuestProgressFile());
            });
        });
    }

}
