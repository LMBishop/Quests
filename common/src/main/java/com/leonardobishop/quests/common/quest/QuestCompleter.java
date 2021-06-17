package com.leonardobishop.quests.common.quest;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;

public interface QuestCompleter {

    void queueSingular(QuestProgress questProgress);
    void queueFullCheck(QuestProgressFile questProgressFile);

}
