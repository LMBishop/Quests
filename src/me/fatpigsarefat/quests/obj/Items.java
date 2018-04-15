package me.fatpigsarefat.quests.obj;

import me.fatpigsarefat.quests.Quests;
import org.bukkit.inventory.ItemStack;

public enum Items {

    BACK_BUTTON("gui.back-button"),
    QUEST_LOCKED("gui.quest-locked-display"),
    QUEST_COOLDOWN("gui.quest-cooldown-display"),
    QUEST_COMPLETED("gui.quest-completed-display");

    String path;

    Items(String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return Quests.getInstance().getItemStack(path);
    }

}
