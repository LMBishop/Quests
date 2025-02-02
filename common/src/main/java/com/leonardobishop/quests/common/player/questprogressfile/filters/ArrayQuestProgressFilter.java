package com.leonardobishop.quests.common.player.questprogressfile.filters;

import org.jetbrains.annotations.NotNull;

public abstract class ArrayQuestProgressFilter implements QuestProgressFilter {

    protected final QuestProgressFilter[] filters;

    public ArrayQuestProgressFilter(final @NotNull QuestProgressFilter @NotNull ... filters) {
        this.filters = filters;
    }
}
