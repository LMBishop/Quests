package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents a cancellation confirmation menu for a specific quest.
 */
public class CancelQMenu implements QMenu {

    private final BukkitQuestsPlugin plugin;
    private final BukkitQuestsConfig config;
    private final QMenu superMenu;
    private final QPlayer owner;
    private final Quest quest;

    public CancelQMenu(BukkitQuestsPlugin plugin, QMenu superMenu, QPlayer owner, Quest quest) {
        this.plugin = plugin;
        this.config = (BukkitQuestsConfig) plugin.getQuestsConfig();
        this.superMenu = superMenu;
        this.owner = owner;
        this.quest = quest;
    }

    public Quest getQuest() {
        return quest;
    }

    @Override
    public QPlayer getOwner() {
        return owner;
    }

    public Inventory toInventory(int page) {
        String title = Chat.color(config.getString("options.guinames.quest-cancel"));

        ItemStack yes = config.getItem("gui.quest-cancel-yes");
        ItemStack no = config.getItem("gui.quest-cancel-no");

        ItemStack background = config.getItem("gui.quest-cancel-background");
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        Inventory inventory = Bukkit.createInventory(null, 27, title);

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, background);
        }

        inventory.setItem(10, no);
        inventory.setItem(11, no);
        inventory.setItem(12, no);
        inventory.setItem(13, plugin.getQItemStackRegistry().getQuestItemStack(quest).toItemStack(quest, owner, owner.getQuestProgressFile().getQuestProgress(quest)));
        inventory.setItem(14, yes);
        inventory.setItem(15, yes);
        inventory.setItem(16, yes);

        return inventory;
    }

    @Override
    public boolean handleClick(InventoryClickEvent event, MenuController controller) {
        if (event.getSlot() == 10 || event.getSlot() == 11 || event.getSlot() == 12) {
            controller.openMenu(event.getWhoClicked(), superMenu, 1);
            return true;
        } else if (event.getSlot() == 14 || event.getSlot() == 15 || event.getSlot() == 16) {
            if (owner.cancelQuest(quest)) {
                event.getWhoClicked().closeInventory();
                return true;
            }
        }
        return false;
    }

    public QMenu getSuperMenu() {
        return superMenu;
    }

}
