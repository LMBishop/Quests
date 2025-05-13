package com.leonardobishop.quests.bukkit.listener;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.quest.Quest;
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
            Messages.BETA_REMINDER.send(event.getPlayer());
        }
        if (plugin.getUpdater().isUpdateReady() && event.getPlayer().hasPermission("quests.admin")) {
            // delay for a bit so they actually see the message
            String updateMessage = Messages.QUEST_UPDATER.getMessageLegacyColor()
                    .replace("{newver}", plugin.getUpdater().getReturnedVersion())
                    .replace("{oldver}", plugin.getUpdater().getInstalledVersion())
                    .replace("{link}", plugin.getUpdater().getUpdateLink());
            plugin.getScheduler().runTaskLaterAtEntity(event.getPlayer(), () -> event.getPlayer().sendMessage(updateMessage), 50L);
        }

        final Player player = event.getPlayer();
        plugin.getQuestsLogger().debug("PlayerJoinListener: " + player.getUniqueId() + " (" + player.getName() + ")");
        plugin.getScheduler().runTaskLater(() -> {
            if (!player.isOnline()) return;
            plugin.getPlayerManager().loadPlayer(player.getUniqueId()).thenAccept(qPlayer -> {
                if (qPlayer == null) return;
                plugin.getScheduler().doSync(() -> {
                    // run a full check to check for any missed quest completions
                    plugin.getQuestCompleter().queueFullCheck(qPlayer.getQuestProgressFile());

                    // track first quest
                    if (plugin.getConfig().getBoolean("options.allow-quest-track") && plugin.getConfig().getBoolean("options.quest-autotrack")) {
                        for (Quest quest : plugin.getQuestManager().getQuestMap().values()) {
                            if (qPlayer.hasStartedQuest(quest)) {
                                qPlayer.trackQuest(quest);
                                break;
                            }
                        }
                    }
                });
            });
        }, plugin.getQuestsConfig().getInt("options.storage.synchronisation.delay-loading", 0));
    }

}
