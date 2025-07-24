package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.element.BackMenuElement;
import com.leonardobishop.quests.bukkit.menu.element.MenuElement;
import com.leonardobishop.quests.bukkit.menu.element.QuestMenuElement;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a menu for a specified category (or all if they are disabled),
 * which contains a listing of different quests.
 */
public class QuestQMenu extends PaginatedQMenu {

    private final String categoryName;

    public QuestQMenu(BukkitQuestsPlugin plugin, QPlayer owner, List<Quest> quests, @Nullable Category category, CategoryQMenu categoryQMenu) {
        super(owner, Chat.legacyColor(guiName(plugin, category)),
                plugin.getQuestsConfig().getBoolean("options.trim-gui-size.quests-menu"), 54, plugin);

        BukkitQuestsConfig config = (BukkitQuestsConfig) plugin.getQuestsConfig();
        this.categoryName = category != null ? category.getId() : null;

        BackMenuElement backMenuElement = categoryQMenu != null
                ? new BackMenuElement(plugin, owner.getPlayerUUID(), plugin.getMenuController(), categoryQMenu)
                : null;

        List<MenuElement> filteredQuests = new ArrayList<>();
        for (Quest quest : quests) {
            if (config.getBoolean("options.gui-hide-locked")) {
                QuestProgress questProgress = owner.getQuestProgressFile().getQuestProgress(quest);
                long cooldown = owner.getQuestProgressFile().getCooldownFor(quest);
                if (!owner.getQuestProgressFile().hasMetRequirements(quest) || (!quest.isRepeatable() && questProgress.isCompletedBefore()) || cooldown > 0) {
                    continue;
                }
            }
            if (config.getBoolean("options.gui-hide-quests-nopermission") && quest.isPermissionRequired()) {
                if (!Bukkit.getPlayer(owner.getPlayerUUID()).hasPermission("quests.quest." + quest.getId())) {
                    continue;
                }
            }
            filteredQuests.add(new QuestMenuElement(plugin, quest, this));
        }

        String path;
        if (categoryName != null) {
            path = "custom-elements.c:" + categoryName;
        } else {
            path = "custom-elements.quests";
        }
        super.populate(path, filteredQuests, backMenuElement);
    }

    public String getCategoryName() {
        return categoryName;
    }

    private static String guiName(final BukkitQuestsPlugin plugin, final @Nullable Category category) {
        if (category != null) {
            final String guiName = category.getGUIName();

            if (guiName != null) {
                return guiName;
            }
        }

        return plugin.getQuestsConfig().getString("options.guinames.quests-menu");
    }
}
