package com.leonardobishop.quests.common.player.questprogressfile.filters;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface QuestProgressFilter {

    QuestProgressFilter ALL = new AllQuestProgressFilter();
    QuestProgressFilter COMPLETED = new CompletedQuestProgressFilter();
    QuestProgressFilter COMPLETED_BEFORE = new CompletedBeforeQuestProgressFilter();
    QuestProgressFilter STARTED = new StartedQuestProgressFilter();

    @Contract(pure = true)
    boolean matches(final @NotNull QuestProgress questProgress);
}
