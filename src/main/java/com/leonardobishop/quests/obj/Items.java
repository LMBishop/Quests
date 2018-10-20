package com.leonardobishop.quests.obj;

import com.leonardobishop.quests.Quests;
import org.bukkit.inventory.ItemStack;

public enum Items {

    BACK_BUTTON("gui.back-button"),
    QUEST_LOCKED("gui.quest-locked-display"),
    QUEST_COOLDOWN("gui.quest-cooldown-display"),
    QUEST_COMPLETED("gui.quest-completed-display"),
    PAGE_PREV("gui.page-prev"),
    PAGE_NEXT("gui.page-next"),
    PAGE_DESCRIPTION("gui.page-desc"),
    QUEST_CANCEL_YES("gui.quest-cancel-yes"),
    QUEST_CANCEL_NO("gui.quest-cancel-no");

    String path;

    Items(String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return Quests.getInstance().getItemStack(path);
    }

}
