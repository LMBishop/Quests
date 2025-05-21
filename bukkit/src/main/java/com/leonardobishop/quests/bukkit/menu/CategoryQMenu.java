package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.element.CategoryMenuElement;
import com.leonardobishop.quests.bukkit.menu.element.MenuElement;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a menu which contains a listing of different categories.
 */
public class CategoryQMenu extends PaginatedQMenu {

    public CategoryQMenu(BukkitQuestsPlugin plugin, QPlayer owner) {
        super(owner, Chat.legacyColor(plugin.getQuestsConfig().getString("options.guinames.quests-category")),
                plugin.getQuestsConfig().getBoolean("options.trim-gui-size.quests-category-menu"), 54, plugin);

        BukkitQuestsConfig config = (BukkitQuestsConfig) plugin.getQuestsConfig();

        List<MenuElement> categoryMenuElements = new ArrayList<>();
        for (Category category : plugin.getQuestManager().getCategories()) {
            if (category.isHidden()) {
                continue;
            }
            if (config.getBoolean("options.gui-hide-categories-nopermission")
                    && category.isPermissionRequired()) {
                if (!Bukkit.getPlayer(owner.getPlayerUUID()).hasPermission("quests.category." + category.getId())) {
                    continue;
                }
            }
            List<Quest> quests = new ArrayList<>();
            for (String questid : category.getRegisteredQuestIds()) {
                Quest quest = plugin.getQuestManager().getQuestById(questid);
                if (quest != null) {
                    quests.add(quest);
                }
            }
            Collections.sort(quests);
            QuestQMenu questQMenu = new QuestQMenu(plugin, owner, quests, category, this);
            MenuElement menuElement = new CategoryMenuElement(plugin, owner.getPlayerUUID(), category, questQMenu);
            categoryMenuElements.add(menuElement);
        }

        super.populate("custom-elements.categories", categoryMenuElements, null);
    }

}
