package com.leonardobishop.quests.obj;

import com.leonardobishop.quests.Quests;
import org.bukkit.inventory.ItemStack;

public enum Items {

    BACK_BUTTON("gui.back-button"),
    QUEST_LOCKED("gui.quest-locked-display"),
    QUEST_COOLDOWN("gui.quest-cooldown-display"),
    QUEST_COMPLETED("gui.quest-completed-display"),
    QUEST_PERMISSION("gui.quest-permission-display"),
    PAGE_PREV("gui.page-prev"),
    PAGE_NEXT("gui.page-next"),
    PAGE_DESCRIPTION("gui.page-desc"),
    QUEST_CANCEL_YES("gui.quest-cancel-yes"),
    QUEST_CANCEL_NO("gui.quest-cancel-no"),
    QUEST_CANCEL_FILLER("gui.quest-cancel-filler");

    private final String path;

    Items(String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return Quests.get().getItemStack(path, Quests.get().getConfig());
    }

}
