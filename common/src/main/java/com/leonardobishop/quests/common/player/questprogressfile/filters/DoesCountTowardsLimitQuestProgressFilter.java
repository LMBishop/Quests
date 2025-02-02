package com.leonardobishop.quests.common.player.questprogressfile.filters;

import com.leonardobishop.quests.common.quest.Quest;
import org.jetbrains.annotations.NotNull;

public final class DoesCountTowardsLimitQuestProgressFilter implements QuestProgressFilter {

    @Override
    public boolean matchesQuest(final @NotNull Quest quest) {
        return quest.doesCountTowardsLimit();
    }
}
