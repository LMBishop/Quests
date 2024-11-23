package com.leonardobishop.quests.common.player;

import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.plugin.Quests;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.questcontroller.QuestController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player.
 */
public final class QPlayer {

    private final Quests plugin;
    private final QPlayerData playerData;
    private QuestController questController;

    public QPlayer(final @NotNull Quests plugin, final @NotNull QPlayerData playerData, final @NotNull QuestController questController) {
        this.plugin = plugin;
        this.playerData = playerData;
        this.questController = questController;
    }

    /**
     * Get this players associated {@link QPlayerData}
     *
     * @return the players data
     */
    public @NotNull QPlayerData getPlayerData() {
        return this.playerData;
    }

    /**
     * Get the player UUID associated with this quest player. The player may not be online.
     *
     * @return uuid
     */
    public @NotNull UUID getPlayerUUID() {
        return this.playerData.playerUUID();
    }

    /**
     * Get this players associated {@link QPlayerPreferences}
     *
     * @return the players preferences
     */
    public @NotNull QPlayerPreferences getPlayerPreferences() {
        return this.playerData.playerPreferences();
    }

    /**
     * Get this players associated {@link QuestProgressFile}
     *
     * @return the quest progress file
     */
    public @NotNull QuestProgressFile getQuestProgressFile() {
        return this.playerData.questProgressFile();
    }

    /**
     * Attempt to complete a quest for the player. This will also play all effects (such as titles, messages etc.)
     * and also dispatches all rewards for the player.
     *
     * @param quest the quest to complete
     * @return true (always)
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean completeQuest(final @NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        return this.questController.completeQuestForPlayer(this, quest);
    }

    /**
     * Attempt to track a quest for the player. This will also play all effects (such as titles, messages etc.)
     *
     * @param quest the quest to track
     */
    public void trackQuest(final @Nullable Quest quest) {
        this.questController.trackQuestForPlayer(this, quest);
    }

    /**
     * Gets whether the player has started a specific quest.
     *
     * @param quest the quest to test for
     * @return true if the quest is started or quest autostart is enabled and the quest is ready to start, false otherwise
     */
    public boolean hasStartedQuest(final @NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        return this.questController.hasPlayerStartedQuest(this, quest);
    }

    /**
     * Gets a list of quests which the player has effectively started. This includes quests started automatically.
     *
     * @return list of effectively started quests
     */
    public @NotNull List<Quest> getEffectiveStartedQuests() {
        return this.getEffectiveStartedQuests(-1);
    }

    /**
     * Gets a list of quests which the player has effectively started with an optional limit on the number of quests.
     * This includes quests started automatically.
     *
     * @param limit the maximum number of quests to return. A value of -1 indicates no limit.
     * @return list of effectively started quests, up to the specified limit
     */
    public @NotNull List<Quest> getEffectiveStartedQuests(final int limit) {
        final Collection<Quest> quests = this.plugin.getQuestManager()
                .getQuests()
                .values();

        final List<Quest> ret;
        if (limit != 1) {
            ret = new ArrayList<>();
        } else {
            ret = null; // for limit 1, use a singleton list which is not backed by an array
        }

        for (final Quest quest : quests) {
            if (this.questController.hasPlayerStartedQuest(this, quest)) {
                if (ret == null) { // this is true only if limit is 1
                    return Collections.singletonList(quest);
                }

                ret.add(quest);

                if (limit != -1 && ret.size() >= limit) { // -1 indicates no limit
                    return ret;
                }
            }
        }

        // if no quests were added to the list and limit was 1, return an empty list
        if (ret == null) {
            return Collections.emptyList();
        }

        return ret;
    }

    /**
     * Attempt to start a quest for the player. This will also play all effects (such as titles, messages etc.)
     * Warning: will fail if the player is not online.
     *
     * @param quest the quest to start
     * @return the quest start result -- {@code QuestStartResult.QUEST_SUCCESS} indicates success
     */
    // TODO PlaceholderAPI support
    public @NotNull QuestStartResult startQuest(final @NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        return this.questController.startQuestForPlayer(this, quest);
    }

    /**
     * Attempt to cancel a quest for the player. This will also play all effects (such as titles, messages etc.)
     *
     * @param quest the quest to start
     * @return true if the quest was cancelled, false otherwise
     */
    public boolean cancelQuest(final @NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        return this.questController.cancelQuestForPlayer(this, quest);
    }

    /**
     * Attempt to expire a quest for the player. This will also play all effects (such as titles, messages etc.)
     *
     * @param quest the quest to start
     * @return true if the quest was expired, false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean expireQuest(final @NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        return this.questController.expireQuestForPlayer(this, quest);
    }

    /**
     * Check if the player can start a quest.
     * Warning: will fail if the player is not online.
     *
     * @param quest the quest to check
     * @return the quest start result
     */
    public @NotNull QuestStartResult canStartQuest(final @NotNull Quest quest) {
        Objects.requireNonNull(quest, "quest cannot be null");

        return this.questController.canPlayerStartQuest(this, quest);
    }

    /**
     * Get player's associated {@link QuestController}. It's usually the server's active quest controller.
     *
     * @see QPlayerManager#getActiveQuestController()
     * @return the quest controller for this player
     */
    public @NotNull QuestController getQuestController() {
        return this.questController;
    }

    /**
     * Sets this players associated {@link QuestController}
     *
     * @param questController new quest controller
     */
    public void setQuestController(final @NotNull QuestController questController) {
        Objects.requireNonNull(questController, "questController cannot be null");

        this.questController = questController;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (o instanceof final QPlayer qPlayer) {
            return this.getPlayerUUID() == qPlayer.getPlayerUUID();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // uuid hash * prime number
        return this.getPlayerUUID().hashCode() * 73;
    }
}
