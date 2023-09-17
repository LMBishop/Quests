package com.leonardobishop.quests.bukkit.tasktype;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.QPlayerPreferences;
import com.leonardobishop.quests.common.tasktype.TaskType;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;

public class BukkitTaskTypeManager extends TaskTypeManager {

    private final BukkitQuestsPlugin plugin;

    public BukkitTaskTypeManager(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public BukkitTaskTypeManager(BukkitQuestsPlugin plugin, List<String> exclusions) {
        super(exclusions);
        this.plugin = plugin;
    }

    @Override
    public boolean registerTaskType(@NotNull TaskType taskType, @NotNull BooleanSupplier... suppliers) {
        if (!(taskType instanceof BukkitTaskType bukkitTaskType)) throw new RuntimeException("BukkitTaskTypeManager implementation can only accept instances of BukkitTaskType!");

        if (super.registerTaskType(taskType)) {
            bukkitTaskType.taskTypeManager = this;
            plugin.getServer().getPluginManager().registerEvents(bukkitTaskType, plugin);
            return true;
        }
        return false;
    }

    public void sendDebug(@NotNull String message, @NotNull String taskType, @NotNull String questId, @NotNull String taskId, @NotNull UUID associatedPlayer) {
        String chatHeader = null;
        for (QPlayer qPlayer : plugin.getPlayerManager().getQPlayers()) {
            QPlayerPreferences.DebugType debugType = qPlayer.getPlayerPreferences().getDebug(questId);
            if (debugType == null) {
                continue;
            }

            Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
            if (player == null) {
                continue;
            }

            if (chatHeader == null) {
                Player otherPlayer = Bukkit.getPlayer(associatedPlayer);
                String associatedName = otherPlayer != null ? otherPlayer.getName() : associatedPlayer.toString();
                chatHeader = ChatColor.GRAY + "[" + associatedName + " - " + questId + "/" + taskId + " - type '" + taskType + "']";
            }

            switch (debugType) {
                case ALL -> {
                    player.sendMessage(chatHeader);
                    player.sendMessage(message);
                }
                case SELF -> {
                    if (player.getUniqueId().equals(associatedPlayer)) {
                        player.sendMessage(chatHeader);
                        player.sendMessage(message);
                    }
                }
            }
        }
    }

}
