package com.leonardobishop.quests.common.player.questprogressfile.filters;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import org.jetbrains.annotations.NotNull;

public final class CompletedQuestProgressFilter implements QuestProgressFilter {

    @Override
    public boolean matchesProgress(final @NotNull QuestProgress questProgress) {
        return questProgress.isCompleted();
    }
}
