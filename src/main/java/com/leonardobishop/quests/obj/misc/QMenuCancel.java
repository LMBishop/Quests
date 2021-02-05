package com.leonardobishop.quests.obj.misc;

import com.leonardobishop.quests.obj.Items;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QMenuCancel implements QMenu {

    private final HashMap<Integer, String> slotsToQuestIds = new HashMap<>();
    private final QMenuQuest superMenu;
    private final QPlayer owner;
    private final Quest quest;

    public QMenuCancel(QPlayer owner, QMenuQuest superMenu, Quest quest) {
        this.owner = owner;
        this.superMenu = superMenu;
        this.quest = quest;
    }

    public void populate(List<Quest> quests) {
        /* ignored */
    }

    @Override
    public HashMap<Integer, String> getSlotsToMenu() {
        return slotsToQuestIds;
    }

    public Quest getQuest() {
        return quest;
    }

    @Override
    public QPlayer getOwner() {
        return owner;
    }

    public Inventory toInventory() {
        String title = Options.color(Options.GUITITLE_QUEST_CANCEL.getStringValue());

        ItemStack yes = Items.QUEST_CANCEL_YES.getItem();
        ItemStack no = Items.QUEST_CANCEL_NO.getItem();

        ItemStack background = Items.QUEST_CANCEL_BACKGROUND.getItem();
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
        inventory.setItem(13, quest.getDisplayItem().toItemStack(quest, owner.getQuestProgressFile(), owner.getQuestProgressFile().getQuestProgress(quest)));
        inventory.setItem(14, yes);
        inventory.setItem(15, yes);
        inventory.setItem(16, yes);

        return inventory;
    }

    public QMenuQuest getSuperMenu() {
        return superMenu;
    }

}
