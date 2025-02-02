package com.leonardobishop.quests.common.player.questprogressfile.filters;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.jetbrains.annotations.NotNull;

public final class NegationQuestProgressFilter implements QuestProgressFilter {

    private final QuestProgressFilter filter;

    public NegationQuestProgressFilter(final @NotNull QuestProgressFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean matchesQuest(final @NotNull Quest quest) {
        return !this.filter.matchesQuest(quest);
    }

    @Override
    public boolean matchesProgress(final @NotNull QuestProgress questProgress) {
        return !this.filter.matchesProgress(questProgress);
    }
}
