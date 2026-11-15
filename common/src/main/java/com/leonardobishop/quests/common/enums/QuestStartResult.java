package com.leonardobishop.quests.common.enums;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum QuestStartResult {
    QUEST_SUCCESS(true), //0
    QUEST_LIMIT_REACHED, //1
    QUEST_ALREADY_COMPLETED, //2
    QUEST_COOLDOWN, //3
    QUEST_LOCKED, //4
    QUEST_ALREADY_STARTED(true), //5
    QUEST_NO_PERMISSION, //6
    NO_PERMISSION_FOR_CATEGORY, //7
    OTHER; //8

    private final boolean playerStartedQuest;

    QuestStartResult(final boolean playerStartedQuest) {
        this.playerStartedQuest = playerStartedQuest;
    }

    QuestStartResult() {
        this(false);
    }

    public boolean hasPlayerStartedQuest() {
        return this.playerStartedQuest;
    }
}
