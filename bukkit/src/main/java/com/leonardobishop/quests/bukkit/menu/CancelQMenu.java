package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.element.AbortCancelMenuElement;
import com.leonardobishop.quests.bukkit.menu.element.ConfirmCancelMenuElement;
import com.leonardobishop.quests.bukkit.menu.element.CustomMenuElement;
import com.leonardobishop.quests.bukkit.menu.element.MenuElement;
import com.leonardobishop.quests.bukkit.menu.element.QuestMenuElement;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

/**
 * Represents a cancellation confirmation menu for a specific quest.
 */
public class CancelQMenu extends QMenu {

    private final BukkitQuestsPlugin plugin;
    private final BukkitQuestsConfig config;
    private final QMenu superMenu;
    private final Quest quest;

    public CancelQMenu(BukkitQuestsPlugin plugin, QMenu superMenu, QPlayer owner, Quest quest) {
        super(owner);
        this.plugin = plugin;
        this.config = (BukkitQuestsConfig) plugin.getQuestsConfig();
        this.superMenu = superMenu;
        this.quest = quest;

        MenuElement questMenuElement = new QuestMenuElement(plugin, quest, this, true);
        MenuElement confirmCancelMenuElement = new ConfirmCancelMenuElement(plugin, owner, quest, superMenu);
        MenuElement abortCancelMenuElement = new AbortCancelMenuElement(plugin, owner, superMenu);
        MenuElement border = new CustomMenuElement(plugin, owner.getPlayerUUID(), config.getItem("gui.quest-cancel-background"));

        for (int i = 0; i < 27; i++) {
            menuElements.put(i, border);
        }

        menuElements.put(10, abortCancelMenuElement);
        menuElements.put(11, abortCancelMenuElement);
        menuElements.put(12, abortCancelMenuElement);
        menuElements.put(13, questMenuElement);
        menuElements.put(14, confirmCancelMenuElement);
        menuElements.put(15, confirmCancelMenuElement);
        menuElements.put(16, confirmCancelMenuElement);
    }

    public Quest getQuest() {
        return quest;
    }

    @Override
    public Inventory draw() {
        String title = Chat.legacyColor(config.getString("options.guinames.quest-cancel"));

        if (config.getBoolean("options.gui-use-placeholderapi")) {
            title = plugin.getPlaceholderAPIProcessor().apply(Bukkit.getPlayer(owner.getPlayerUUID()), title);
        }

        Inventory inventory = Bukkit.createInventory(null, 27, title);

        for (int pointer = 0; pointer < 27; pointer++) {
            if (menuElements.containsKey(pointer)) {
                inventory.setItem(pointer, menuElements.get(pointer).asItemStack());
            }
        }

        return inventory;
    }
}
