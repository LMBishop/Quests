package com.leonardobishop.quests.common.player;

import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.plugin.Quests;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.questcontroller.QuestController;

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

    public UUID getPlayerUUID() {
        return this.uuid;
    }

    /**
     * Attempt to complete a quest for the player. This will also play all effects (such as titles, messages etc.)
     * and also dispatches all rewards for the player.
     *
     * @param quest the quest to complete
     * @return true (always)
     */
    public boolean completeQuest(Quest quest) {
        return questController.completeQuestForPlayer(this, quest);
    }

    /**
     * Attempt to track a quest for the player. This will also play all effects (such as titles, messages etc.)
     **
     * @param quest the quest to track
     */
    public void trackQuest(Quest quest) {
        questController.trackQuestForPlayer(this, quest);
    }

    /**
     * Gets whether or not the player has started a specific quest.
     *
     * @param quest the quest to test for
     * @return true if the quest is started or quest autostart is enabled and the quest is ready to start, false otherwise
     */
    public boolean hasStartedQuest(Quest quest) {
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
    public QuestStartResult startQuest(Quest quest) {
        return questController.startQuestForPlayer(this, quest);
    }

    /**
     * Attempt to cancel a quest for the player. This will also play all effects (such as titles, messages etc.)
     *
     * @param quest the quest to start
     * @return true if the quest was cancelled, false otherwise
     */
    public boolean cancelQuest(Quest quest) {
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
    public QuestStartResult canStartQuest(Quest quest) {
        return questController.canPlayerStartQuest(this, quest);
    }


    public QuestProgressFile getQuestProgressFile() {
        return questProgressFile;
    }

    public QPlayerPreferences getPlayerPreferences() {
        return playerPreferences;
    }

    public QuestController getQuestController() {
        return questController;
    }

    public void setQuestController(QuestController questController) {
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
