package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.menu.QuestQMenu;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.common.quest.Category;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CategoryMenuElement extends MenuElement {

    private final BukkitQuestsPlugin plugin;
    private final UUID owner;
    private final QuestQMenu questMenu;

    public CategoryMenuElement(BukkitQuestsPlugin plugin, UUID owner, QuestQMenu questMenu) {
        this.plugin = plugin;
        this.owner = owner;
        this.questMenu = questMenu;
    }

    public UUID getOwner() {
        return owner;
    }

    public QuestQMenu getQuestMenu() {
        return questMenu;
    }

    @Override
    public ItemStack asItemStack() {
        Category category = plugin.getQuestManager().getCategoryById(questMenu.getCategoryName());
        if (category != null) {
            return MenuUtils.applyPlaceholders(plugin, owner, plugin.getQItemStackRegistry().getCategoryItemStack(category));
        }
        return null;
    }
}
