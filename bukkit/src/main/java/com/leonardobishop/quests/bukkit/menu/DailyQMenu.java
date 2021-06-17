//package com.leonardobishop.quests.bukkit.menu;
//
//import com.leonardobishop.quests.Quests;
//import com.leonardobishop.quests.common.enums.QuestStartResult;
//import com.leonardobishop.quests.common.player.QPlayer;
//import com.leonardobishop.quests.common.quest.Quest;
//import com.leonardobishop.quests.listener.MenuController;
//import com.leonardobishop.quests.menu.element.MenuElement;
//import com.leonardobishop.quests.menu.element.QuestMenuElement;
//import com.leonardobishop.quests.quest.controller.DailyQuestController;
//import com.leonardobishop.quests.util.Items;
//import com.leonardobishop.quests.util.Options;
//import org.bukkit.Bukkit;
//import org.bukkit.event.inventory.ClickType;
//import org.bukkit.event.inventory.InventoryClickEvent;
//import org.bukkit.inventory.Inventory;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Represents a cancellation confirmation menu for a specific quest.
// */
//public class DailyQMenu implements QMenu {
//
//    private final Map<Integer, MenuElement> menuElements = new HashMap<>();
//    private final Quests plugin;
//    private final QPlayer owner;
//
//    public DailyQMenu(Quests plugin, QPlayer owner) {
//        this.plugin = plugin;
//        this.owner = owner;
//    }
//
//    @Override
//    public QPlayer getOwner() {
//        return owner;
//    }
//
//    public void populate() {
//        if (!(owner.getQuestController() instanceof DailyQuestController)) {
//            return;
//        }
//        DailyQuestController dailyQuestController = (DailyQuestController) owner.getQuestController();
//        List<String> quests = dailyQuestController.getQuests();
//        for (int i = 0; i < quests.size(); i++) {
//            menuElements.put(11 + i, new QuestMenuElement(plugin, owner, quests.get(i)));
//        }
//    }
//
//    public Inventory toInventory(int page) {
//        String title = Options.color(Options.GUITITLE_DAILY_QUESTS.getStringValue());
//
//        ItemStack background = Items.QUEST_CANCEL_BACKGROUND.getItem();
//        ItemMeta backgroundMeta = background.getItemMeta();
//        backgroundMeta.setDisplayName(" ");
//        background.setItemMeta(backgroundMeta);
//
//        Inventory inventory = Bukkit.createInventory(null, 27, title);
//
//        for (int i = 0; i < inventory.getSize(); i++) {
//            inventory.setItem(i, background);
//        }
//
//        for (int pointer = 0; pointer < 27; pointer++) {
//            if (menuElements.containsKey(pointer)) {
//                inventory.setItem(pointer, menuElements.get(pointer).asItemStack());
//            }
//        }
//
//        return inventory;
//    }
//
//    @Override
//    public void handleClick(InventoryClickEvent event, MenuController controller) {
//        if (menuElements.containsKey(event.getSlot())) {
//            MenuElement menuElement = menuElements.get(event.getSlot());
//            if (menuElement instanceof QuestMenuElement) {
//                QuestMenuElement questMenuElement = (QuestMenuElement) menuElement;
//                Quest quest = plugin.getQuestManager().getQuestById(questMenuElement.getQuestId());
//                if (event.getClick() == ClickType.LEFT) {
//                    if (Options.QUEST_AUTOSTART.getBooleanValue()) return;
//                    if (owner.startQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
//                        event.getWhoClicked().closeInventory(); //TODO Option to keep the menu open
//                    }
//                } else if (event.getClick() == ClickType.MIDDLE && Options.ALLOW_QUEST_TRACK.getBooleanValue()) {
//                    MenuUtil.handleMiddleClick(this, quest, Bukkit.getPlayer(owner.getPlayerUUID()), controller);
//                }
//            }
//        }
//    }
//
//
//}
