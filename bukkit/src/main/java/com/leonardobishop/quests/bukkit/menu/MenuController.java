package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.menu.element.MenuElement;
import com.leonardobishop.quests.bukkit.util.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.UUID;

public class MenuController implements Listener {

    private final HashMap<UUID, QMenu> tracker = new HashMap<>();
    private final BukkitQuestsPlugin plugin;

    public MenuController(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public void openMenu(UUID uuid, QMenu qMenu) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        openMenu(player, qMenu);
    }

    public void openMenu(HumanEntity player, QMenu qMenu) {
        SoundUtils.playSoundForPlayer((Player) player, plugin.getQuestsConfig().getString("options.sounds.gui.open"));
//        Bukkit.getScheduler().runTaskLater(plugin, () -> SoundUtils.playSoundForPlayer((Player) player, plugin.getQuestsConfig().getString("options.sounds.gui.open")), 1L);
        player.openInventory(qMenu.draw());
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
            MenuElement menuElement = qMenu.getMenuElementAt(event.getSlot());
            if (menuElement == null) {
                return;
            }
            ClickResult result = menuElement.handleClick(player, event.getClick());
            if (result == ClickResult.DO_NOTHING) {
                return;
            }
            SoundUtils.playSoundForPlayer(player, plugin.getQuestsConfig().getString("options.sounds.gui.interact"));
            if (result == ClickResult.REFRESH_PANE) {
                player.openInventory(qMenu.draw());
                tracker.put(player.getUniqueId(), qMenu);
            } else if (result == ClickResult.CLOSE_MENU) {
                player.closeInventory();
            }
        }
    }

//    /**
//     * Open the started menu for the player
//     *
//     * @param qPlayer player
//     */
//    public void openStartedQuests(QPlayer qPlayer) {
//        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
//        if (player == null) {
//            return;
//        }
//
//        StartedQMenu startedQMenu = new StartedQMenu(plugin, qPlayer);
//        List<QuestSortWrapper> quests = new ArrayList<>();
//        for (Map.Entry<String, Quest> entry : plugin.getQuestManager().getQuestMap().entrySet()) {
//            quests.add(new QuestSortWrapper(plugin, entry.getValue()));
//        }
//        startedQMenu.populate(quests);
//
//        openMenu(player, startedQMenu, 1);
//    }

}
