package com.leonardobishop.quests.common.quest;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import org.jspecify.annotations.NullMarked;

/**
 * The quest completer is responsible for checking each player for completed quests. Implementations may split
 * this workload up into a queue based system.
 */
@NullMarked
public interface QuestCompleter {

    /**
     * Queue an individual quest progress to check if the quest is completed.
     *
     * @param questProgress the questprogress to check
     */
    void queueSingular(QuestProgress questProgress);

    /**
     * Queue a quest progress file for a full check if they have completed any quests.
     *
     * @param questProgressFile the questprogressfile to check
     */
    void queueFullCheck(QuestProgressFile questProgressFile);
}
