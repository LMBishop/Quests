package com.leonardobishop.quests.util;

import com.leonardobishop.quests.Quests;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public enum Items {

    BACK_BUTTON("gui.back-button"),
    QUEST_LOCKED("gui.quest-locked-display"),
    QUEST_COOLDOWN("gui.quest-cooldown-display"),
    QUEST_COMPLETED("gui.quest-completed-display"),
    QUEST_PERMISSION("gui.quest-permission-display"),
    PAGE_PREV("gui.page-prev"),
    PAGE_NEXT("gui.page-next"),
    PAGE_DESCRIPTION("gui.page-desc"),
    NO_STARTED_QUESTS("gui.no-started-quests"),
    QUEST_CANCEL_YES("gui.quest-cancel-yes"),
    QUEST_CANCEL_NO("gui.quest-cancel-no"),
    QUEST_CANCEL_BACKGROUND("gui.quest-cancel-background");

    private static final Map<String, ItemStack> cachedItemStacks = new HashMap<>();

    private final String path;

    Items(String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return new ItemStack(cachedItemStacks.computeIfAbsent(path, s -> Quests.get().getItemStack(path, Quests.get().getConfig())));
    }

}
