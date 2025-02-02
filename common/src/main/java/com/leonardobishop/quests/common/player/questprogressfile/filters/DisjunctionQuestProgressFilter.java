package com.leonardobishop.quests.common.player.questprogressfile.filters;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import org.jetbrains.annotations.NotNull;

public final class DisjunctionQuestProgressFilter extends ArrayQuestProgressFilter {

    DisjunctionQuestProgressFilter(final @NotNull QuestProgressFilter @NotNull ... filters) {
        super(filters);
    }

    @Override
    public boolean matches(final @NotNull QuestProgress questProgress) {
        for (final QuestProgressFilter filter : this.filters) {
            if (filter.matches(questProgress)) {
                return true;
            }
        }

        return false;
    }
}
