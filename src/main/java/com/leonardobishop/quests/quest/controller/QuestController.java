package com.leonardobishop.quests.quest.controller;

import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.quest.Quest;

public interface QuestController {

    QuestStartResult startQuestForPlayer(QPlayer qPlayer, Quest quest);
    QuestStartResult canPlayerStartQuest(QPlayer qPlayer, Quest quest);
    boolean completeQuestForPlayer(QPlayer qPlayer, Quest quest);
    boolean hasPlayerStartedQuest(QPlayer qPlayer, Quest quest);
    boolean cancelQuestForPlayer(QPlayer qPlayer, Quest quest);

}
