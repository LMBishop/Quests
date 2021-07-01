package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.SoundUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuController implements Listener {

    private final HashMap<UUID, QMenu> tracker = new HashMap<>();
    private final BukkitQuestsPlugin plugin;

    public MenuController(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public void openMenu(HumanEntity player, QMenu qMenu, int page) {
        SoundUtils.playSoundForPlayer((Player) player, plugin.getQuestsConfig().getString("options.sounds.gui.open"));
//        Bukkit.getScheduler().runTaskLater(plugin, () -> SoundUtils.playSoundForPlayer((Player) player, plugin.getQuestsConfig().getString("options.sounds.gui.open")), 1L);
        player.openInventory(qMenu.toInventory(page));
        tracker.put(player.getUniqueId(), qMenu);
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        tracker.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        // check if the player has a quest menu open
        Player player = (Player) event.getWhoClicked();
        if (tracker.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            if (event.getClickedInventory() == null)
                return; //The player clicked outside the inventory
            if (event.getClickedInventory().getType() == InventoryType.PLAYER)
                return; //The clicked inventory is a player inventory type

            QMenu qMenu = tracker.get(player.getUniqueId());
            if (qMenu.handleClick(event, this)) {
                SoundUtils.playSoundForPlayer(player, plugin.getQuestsConfig().getString("options.sounds.gui.interact"));
//                Bukkit.getScheduler().runTaskLater(plugin, () -> SoundUtils.playSoundForPlayer(player, plugin.getQuestsConfig().getString("options.sounds.gui.interact")), 1L);
            }
        }
    }

    /**
     * Opens a quest listing menu for the player.
     *
     * @return 0 if success, 1 if no permission, 2 is only data loaded, 3 if player not found
     */
    public int openQuestCategory(QPlayer qPlayer, Category category, CategoryQMenu superMenu, boolean backButton) {
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (player == null) {
            return 3;
        }

        if (category.isPermissionRequired() && !player.hasPermission("quests.category." + category.getId())) {
            return 1;
        }

        // Using `this` instead of searching again for this QPlayer
        QuestQMenu questQMenu = new QuestQMenu(plugin, qPlayer, category.getId(), superMenu);
        List<Quest> quests = new ArrayList<>();
        for (String questid : category.getRegisteredQuestIds()) {
            Quest quest = plugin.getQuestManager().getQuestById(questid);
            if (quest != null) {
                quests.add(quest);
            }
        }
        questQMenu.populate(quests);
        questQMenu.setBackButtonEnabled(backButton);

        openMenu(player, questQMenu, 1);
        return 0;
    }

    /**
     * Opens a specific quest listing menu for the player.
     *
     * @return 0 if success, 1 if no permission, 2 is only data loaded, 3 if player not found
     */
    public int openQuestCategory(QPlayer qPlayer, Category category, QuestQMenu questQMenu) {
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (player == null) {
            return 3;
        }

        if (category.isPermissionRequired() && !player.hasPermission("quests.category." + category.getId())) {
            return 1;
        }

        openMenu(player, questQMenu, 1);
        return 0;
    }

    /**
     * Open the main menu for the player
     *
     * @param qPlayer player
     */
    public void openMainMenu(QPlayer qPlayer) {
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (player == null) {
            return;
        }

        if (plugin.getQuestController().getName().equals("normal")) {
            if (plugin.getQuestsConfig().getBoolean("options.categories-enabled")) {
                CategoryQMenu categoryQMenu = new CategoryQMenu(plugin, qPlayer);
                List<QuestQMenu> questMenus = new ArrayList<>();
                for (Category category : plugin.getQuestManager().getCategories()) {
                    QuestQMenu questQMenu = new QuestQMenu(plugin, qPlayer, category.getId(), categoryQMenu);
                    List<Quest> quests = new ArrayList<>();
                    for (String questid : category.getRegisteredQuestIds()) {
                        Quest quest = plugin.getQuestManager().getQuestById(questid);
                        if (quest != null) {
                            quests.add(quest);
                        }
                    }
                    questQMenu.populate(quests);
                    questMenus.add(questQMenu);
                }
                categoryQMenu.populate(questMenus);

                openMenu(player, categoryQMenu, 1);
            } else {
                QuestQMenu questQMenu = new QuestQMenu(plugin, qPlayer, "", null);
                List<Quest> quests = new ArrayList<>();
                for (Map.Entry<String, Quest> entry : plugin.getQuestManager().getQuests().entrySet()) {
                    quests.add(entry.getValue());
                }
                questQMenu.populate(quests);
                questQMenu.setBackButtonEnabled(false);

                openMenu(player, questQMenu, 1);
            }
        }
//        } else {
//            DailyQMenu dailyQMenu = new DailyQMenu(plugin, this);
//            dailyQMenu.populate();
//            plugin.getMenuController().openMenu(player, dailyQMenu, 1);
//        }
    }

    /**
     * Open the started menu for the player
     *
     * @param qPlayer player
     */
    public void openStartedQuests(QPlayer qPlayer) {
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (player == null) {
            return;
        }

        StartedQMenu startedQMenu = new StartedQMenu(plugin, qPlayer);
        List<QuestSortWrapper> quests = new ArrayList<>();
        for (Map.Entry<String, Quest> entry : plugin.getQuestManager().getQuests().entrySet()) {
            quests.add(new QuestSortWrapper(plugin, entry.getValue()));
        }
        startedQMenu.populate(quests);

        openMenu(player, startedQMenu, 1);
    }

}
