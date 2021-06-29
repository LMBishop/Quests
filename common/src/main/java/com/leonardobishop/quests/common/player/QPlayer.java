package com.leonardobishop.quests.common.player;

import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.plugin.Quests;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.questcontroller.QuestController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player.
 */
public class QPlayer {

    private final Quests plugin;
    private final UUID uuid;
    private final QPlayerPreferences playerPreferences;
    private final QuestProgressFile questProgressFile;
    private QuestController questController;

    public QPlayer(Quests plugin, UUID uuid, QPlayerPreferences playerPreferences, QuestProgressFile questProgressFile, QuestController questController) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.playerPreferences = playerPreferences;
        this.questProgressFile = questProgressFile;
        this.questController = questController;
    }

    /**
     * Get the player UUID associated with this quest player. The player may not be online.
     *
     * @return uuid
     */
    public @NotNull UUID getPlayerUUID() {
        return this.uuid;
    }

    /**
     * Attempt to complete a quest for the player. This will also play all effects (such as titles, messages etc.)
     * and also dispatches all rewards for the player.
     *
     * @param quest the quest to complete
     * @return true (always)
     */
    public boolean completeQuest(@NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        return questController.completeQuestForPlayer(this, quest);
    }

    /**
     * Attempt to track a quest for the player. This will also play all effects (such as titles, messages etc.)
     **
     * @param quest the quest to track
     */
    public void trackQuest(@Nullable Quest quest) {
        questController.trackQuestForPlayer(this, quest);
    }

    /**
     * Gets whether or not the player has started a specific quest.
     *
     * @param quest the quest to test for
     * @return true if the quest is started or quest autostart is enabled and the quest is ready to start, false otherwise
     */
    public boolean hasStartedQuest(@NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        return questController.hasPlayerStartedQuest(this, quest);
    }

    /**
     * Attempt to start a quest for the player. This will also play all effects (such as titles, messages etc.)
     *
     * Warning: will fail if the player is not online.
     *
     * @param quest the quest to start
     * @return the quest start result -- {@code QuestStartResult.QUEST_SUCCESS} indicates success
     */
    // TODO PlaceholderAPI support
    public @NotNull QuestStartResult startQuest(@NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        return questController.startQuestForPlayer(this, quest);
    }

    /**
     * Attempt to cancel a quest for the player. This will also play all effects (such as titles, messages etc.)
     *
     * @param quest the quest to start
     * @return true if the quest was cancelled, false otherwise
     */
    public boolean cancelQuest(@NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        return questController.cancelQuestForPlayer(this, quest);
    }

    /**
     * Check if the player can start a quest.
     *
     * Warning: will fail if the player is not online.
     *
     * @param quest the quest to check
     * @return the quest start result
     */
    public @NotNull QuestStartResult canStartQuest(@NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        return questController.canPlayerStartQuest(this, quest);
    }

    /**
     * Get this players associated {@link QuestProgressFile}
     *
     * @return the quest progress file
     */
    public @NotNull QuestProgressFile getQuestProgressFile() {
        return questProgressFile;
    }

    /**
     * Get this players associated {@link QPlayerPreferences}
     *
     * @return the players preferences
     */
    public @NotNull QPlayerPreferences getPlayerPreferences() {
        return playerPreferences;
    }

    /**
     * Get this players associated {@link QuestController}, usually the servers active quest controller
     *
     * @see QPlayerManager#getActiveQuestController()
     * @return the quest controller for this player
     */
    public @NotNull QuestController getQuestController() {
        return questController;
    }

    /**
     * Sets this players associated {@link QuestController}
     *
     * @param questController new quest controller
     */
    public void setQuestController(@NotNull QuestController questController) {
        Objects.requireNonNull(questController, "questController cannot be null");

        this.questController = questController;
    }

    @Override //Used by java GC
    public boolean equals(Object o) {
        if (!(o instanceof QPlayer)) return false;
        QPlayer qPlayer = (QPlayer) o;
        return this.uuid == qPlayer.getPlayerUUID();
    }

    @Override //Used by java GC
    public int hashCode() {
        return uuid.hashCode() * 73; //uuid hash * prime number
    }
}
