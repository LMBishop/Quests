package com.leonardobishop.quests.common.player.questprogressfile.filters;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import org.jetbrains.annotations.NotNull;

public final class StartedQuestProgressFilter implements QuestProgressFilter {

    StartedQuestProgressFilter() {
    }

    @Override
    public boolean matches(final @NotNull QuestProgress questProgress) {
        return questProgress.isStarted();
    }
}
