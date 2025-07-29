package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.menu.ClickResult;
import com.leonardobishop.quests.bukkit.menu.QuestQMenu;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.quest.Category;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CategoryMenuElement extends MenuElement {

    private final BukkitQuestsPlugin plugin;
    private final UUID owner;
    private final Category category;
    private final QuestQMenu questMenu;

    public CategoryMenuElement(BukkitQuestsPlugin plugin, UUID owner, Category category, QuestQMenu questMenu) {
        this.plugin = plugin;
        this.owner = owner;
        this.category = category;
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

    @Override
    public ClickResult handleClick(Player whoClicked, ClickType clickType) {
        Player player = Bukkit.getPlayer(owner);
        if (player == null) {
            return ClickResult.DO_NOTHING;
        }

        if (category.isPermissionRequired() && !player.hasPermission("quests.category." + category.getId())) {
            Messages.QUEST_CATEGORY_QUEST_PERMISSION.send(player);
            return ClickResult.DO_NOTHING;
        }

        plugin.getMenuController().openMenu(owner, questMenu);
        return ClickResult.DO_NOTHING;
    }
}
