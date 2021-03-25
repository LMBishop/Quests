package com.leonardobishop.quests.menu;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.quests.Quest;

public class QuestSortWrapper implements Comparable<QuestSortWrapper> {

    private int weightedSortOrder;
    private final Quest quest;

    public QuestSortWrapper(Quests plugin, Quest quest) {
        this.quest = quest;
        if (quest.getCategoryId() == null) {
            weightedSortOrder = quest.getSortOrder();
            return;
        }
        Category c = plugin.getQuestManager().getCategoryById(quest.getCategoryId());
        if (c != null) {
            int index = plugin.getQuestManager().getCategories().indexOf(c);
            int amountBelow = 0;
            //TODO precalculate
            for (int i = index; i > 0; i--) {
                Category below = plugin.getQuestManager().getCategories().get(i - 1);
                amountBelow += below.getRegisteredQuestIds().size();
            }
            weightedSortOrder = amountBelow + quest.getSortOrder();
        }
    }

    public int getWeightedSortOrder() {
        return weightedSortOrder;
    }

    public Quest getQuest() {
        return quest;
    }

    @Override
    public int compareTo(QuestSortWrapper quest) {
        return (weightedSortOrder - quest.weightedSortOrder);
    }

}
