package com.leonardobishop.quests.bukkit.menu.itemstack;

import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to store itemstacks for use within Quests menus.
 */
public class QItemStackRegistry {

    private final Map<String, QItemStack> questRegistry = new HashMap<>();
    private final Map<String, ItemStack> categoryRegistry = new HashMap<>();

    private final Map<String, ItemStack> questLockedRegistry = new HashMap<>();
    private final Map<String, ItemStack> questCompletedRegistry = new HashMap<>();
    private final Map<String, ItemStack> questCooldownRegistry = new HashMap<>();
    private final Map<String, ItemStack> questPermissionRegistry = new HashMap<>();

    public QItemStack getQuestItemStack(Quest quest) {
        return questRegistry.get(quest.getId());
    }

    public ItemStack getQuestLockedItemStack(Quest quest) {
        return questLockedRegistry.get(quest.getId());
    }

    public ItemStack getQuestCompletedItemStack(Quest quest) {
        return questCompletedRegistry.get(quest.getId());
    }

    public ItemStack getQuestCooldownItemStack(Quest quest) {
        return questCooldownRegistry.get(quest.getId());
    }

    public ItemStack getQuestPermissionItemStack(Quest quest) {
        return questPermissionRegistry.get(quest.getId());
    }

    public boolean hasQuestLockedItemStack(Quest quest) {
        return questLockedRegistry.containsKey(quest.getId());
    }

    public boolean hasQuestCompletedItemStack(Quest quest) {
        return questCompletedRegistry.containsKey(quest.getId());
    }

    public boolean hasQuestCooldownItemStack(Quest quest) {
        return questCooldownRegistry.containsKey(quest.getId());
    }

    public boolean hasQuestPermissionItemStack(Quest quest) {
        return questPermissionRegistry.containsKey(quest.getId());
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

    public void registerQuestLocked(Quest quest, ItemStack itemStack) {
        questLockedRegistry.put(quest.getId(), itemStack);
    }

    public void registerQuestCompleted(Quest quest, ItemStack itemStack) {
        questCompletedRegistry.put(quest.getId(), itemStack);
    }

    public void registerQuestCooldown(Quest quest, ItemStack itemStack) {
        questCooldownRegistry.put(quest.getId(), itemStack);
    }

    public void registerQuestPermission(Quest quest, ItemStack itemStack) {
        questPermissionRegistry.put(quest.getId(), itemStack);
    }

    public void register(Category quest, ItemStack itemStack) {
        categoryRegistry.put(quest.getId(), itemStack);
    }

}
