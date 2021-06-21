package com.leonardobishop.quests.common.quest;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;

/**
 * The quest completer is responsible for checking each player for completed quests. Implementations may split
 * this workload up into a queue based system.
 */
public interface QuestCompleter {

    void queueSingular(QuestProgress questProgress);
    void queueFullCheck(QuestProgressFile questProgressFile);

}
