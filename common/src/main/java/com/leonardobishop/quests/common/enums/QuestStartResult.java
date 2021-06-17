package com.leonardobishop.quests.common.enums;

public enum QuestStartResult {
    QUEST_SUCCESS, //0
    QUEST_LIMIT_REACHED, //1
    QUEST_ALREADY_COMPLETED, //2
    QUEST_COOLDOWN, //3
    QUEST_LOCKED, //4
    QUEST_ALREADY_STARTED, //5
    QUEST_NO_PERMISSION, //6
    NO_PERMISSION_FOR_CATEGORY, //7
    OTHER //8
}
