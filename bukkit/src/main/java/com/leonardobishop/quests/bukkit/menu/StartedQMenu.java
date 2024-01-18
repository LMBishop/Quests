package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.menu.element.MenuElement;
import com.leonardobishop.quests.bukkit.menu.element.QuestMenuElement;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Quest;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a menu listing quests a player has started.
 */
public class StartedQMenu extends PaginatedQMenu {

    public StartedQMenu(BukkitQuestsPlugin plugin, QPlayer owner, List<Quest> quests) {
        super(owner, Chat.legacyColor(plugin.getQuestsConfig().getString("options.guinames.quests-started-menu")),
                plugin.getQuestsConfig().getBoolean("options.trim-gui-size.quests-started-menu"), 54, plugin);

        List<MenuElement> elements = new ArrayList<>();

        for (Quest quest : quests) {
            if (!owner.hasStartedQuest(quest)) {
                continue;
            }
            elements.add(new QuestMenuElement(plugin, quest, this));
        }

        super.populate(null, elements, null);
    }

}
