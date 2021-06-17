package com.leonardobishop.quests.common.questcontroller;

import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Quest;

public interface QuestController {

    String getName();

    QuestStartResult startQuestForPlayer(QPlayer qPlayer, Quest quest);

    QuestStartResult canPlayerStartQuest(QPlayer qPlayer, Quest quest);

    boolean completeQuestForPlayer(QPlayer qPlayer, Quest quest);

    boolean hasPlayerStartedQuest(QPlayer qPlayer, Quest quest);

    boolean cancelQuestForPlayer(QPlayer qPlayer, Quest quest);

    void trackQuestForPlayer(QPlayer qPlayer, Quest quest);

}
