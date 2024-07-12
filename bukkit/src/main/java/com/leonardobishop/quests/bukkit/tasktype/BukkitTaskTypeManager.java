package com.leonardobishop.quests.bukkit.tasktype;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.QPlayerPreferences;
import com.leonardobishop.quests.common.tasktype.TaskType;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * The Bukkit task type manager stores all registered Bukkit-specific task types and registers individual quests to each task type.
 * This manager ensures that task types specific to Bukkit are registered and handled correctly.
 */
public final class BukkitTaskTypeManager extends TaskTypeManager {

    private final BukkitQuestsPlugin plugin;

    /**
     * Constructs a new BukkitTaskTypeManager with exclusions.
     *
     * @param plugin the Bukkit plugin instance
     * @param exclusions the set of task type exclusions
     */
    public BukkitTaskTypeManager(final @NotNull BukkitQuestsPlugin plugin, final @NotNull Set<String> exclusions) {
        super(exclusions);
        Objects.requireNonNull(plugin, "plugin cannot be null");

        this.plugin = plugin;
    }

    /**
     * Constructs a new BukkitTaskTypeManager.
     *
     * @param plugin the Bukkit plugin instance
     */
    @SuppressWarnings("unused")
    public BukkitTaskTypeManager(final @NotNull BukkitQuestsPlugin plugin) {
        super();
        Objects.requireNonNull(plugin, "plugin cannot be null");

        this.plugin = plugin;
    }

    /**
     * Registers a Bukkit-specific task type with the task type manager.
     *
     * @param taskType the task type to register
     * @return true if the task type was successfully registered, false otherwise
     * @throws UnsupportedOperationException if the task type is not an instance of {@link BukkitTaskType}
     */
    @Override
    public boolean registerTaskType(final @NotNull TaskType taskType) {
        if (!(taskType instanceof final BukkitTaskType bukkitTaskType)) {
            throw new UnsupportedOperationException("BukkitTaskTypeManager implementation can only accept instances of BukkitTaskType!");
        }

        if (super.registerTaskType(taskType)) {
            bukkitTaskType.taskTypeManager = this;
            this.plugin.getServer().getPluginManager().registerEvents(bukkitTaskType, this.plugin);
            return true;
        }

        return false;
    }

    /**
     * Sends a debug message to players based on their debug preferences.
     *
     * @param message the debug message to send
     * @param taskType the type of task
     * @param questId the quest ID
     * @param taskId the task ID
     * @param associatedPlayer the UUID of the associated player
     */
    @Override
    public void sendDebug(final @NotNull String message, final @NotNull String taskType, final @NotNull String questId, final @NotNull String taskId, final @NotNull UUID associatedPlayer) {
        String chatHeader = null;

        for (final QPlayer qPlayer : QPlayerPreferences.getDebuggers()) {
            final QPlayerPreferences.DebugType debugType = qPlayer.getPlayerPreferences().getDebug(questId);
            if (debugType == null) {
                continue;
            }

            final Player player = this.plugin.getServer().getPlayer(qPlayer.getPlayerUUID());
            if (player == null) {
                continue;
            }

            if (chatHeader == null) {
                final Player otherPlayer = this.plugin.getServer().getPlayer(associatedPlayer);
                final String associatedName = otherPlayer != null ? otherPlayer.getName() : associatedPlayer.toString();
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
