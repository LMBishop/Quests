package com.leonardobishop.quests.bukkit.menu.itemstack;

import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class QItemStackRegistry {

    private final Map<String, QItemStack> questRegistry = new HashMap<>();
    private final Map<String, ItemStack> categoryRegistry = new HashMap<>();

    public QItemStack getQuestItemStack(Quest quest) {
        return questRegistry.get(quest.getId());
    }

    public ItemStack getCategoryItemStack(Category category) {
        return categoryRegistry.get(category.getId());
    }

    public void clearRegistry() {
        questRegistry.clear();
        categoryRegistry.clear();
    }

    public void register(Quest quest, QItemStack qItemStack) {
        questRegistry.put(quest.getId(), qItemStack);
    }

    public void register(Category quest, ItemStack itemStack) {
        categoryRegistry.put(quest.getId(), itemStack);
    }

}
