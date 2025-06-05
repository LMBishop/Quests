package com.leonardobishop.quests.common.player.questprogressfile.filters;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface QuestProgressFilter {

    // QuestProgress filters
    QuestProgressFilter ALL = new AllQuestProgressFilter();
    QuestProgressFilter COMPLETED = new CompletedQuestProgressFilter();
    QuestProgressFilter COMPLETED_BEFORE = new CompletedBeforeQuestProgressFilter();
    QuestProgressFilter STARTED = new StartedQuestProgressFilter();

    // Quest filters
    QuestProgressFilter DOES_COUNT_TOWARDS_COMPLETED = new DoesCountTowardsCompletedQuestProgressFilter();
    QuestProgressFilter DOES_COUNT_TOWARDS_LIMIT = new DoesCountTowardsLimitQuestProgressFilter();

    // Counting
    QuestProgressFilter COMPLETED_COUNT = new ConjunctionQuestProgressFilter(QuestProgressFilter.COMPLETED, QuestProgressFilter.DOES_COUNT_TOWARDS_COMPLETED);
    QuestProgressFilter COMPLETED_BEFORE_COUNT = new ConjunctionQuestProgressFilter(QuestProgressFilter.COMPLETED_BEFORE, QuestProgressFilter.DOES_COUNT_TOWARDS_COMPLETED);

    @Contract(pure = true)
    default boolean matchesQuest(final @NotNull Quest quest) {
        return true;
    }

    @Contract(pure = true)
    default boolean matchesProgress(final @NotNull QuestProgress questProgress) {
        return true;
    }
}
