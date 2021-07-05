package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.menu.CancelQMenu;
import com.leonardobishop.quests.bukkit.menu.MenuController;
import com.leonardobishop.quests.bukkit.menu.QMenu;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuUtils {

    public static ItemStack applyPlaceholders(BukkitQuestsPlugin plugin, UUID owner, ItemStack is) {
        return applyPlaceholders(plugin, owner, is, Collections.emptyMap());
    }

    public static ItemStack applyPlaceholders(BukkitQuestsPlugin plugin, UUID owner, ItemStack is, Map<String, String> placeholders) {
        ItemStack newItemStack = is.clone();
        List<String> lore = newItemStack.getItemMeta().getLore();
        List<String> newLore = new ArrayList<>();
        ItemMeta ism = newItemStack.getItemMeta();
        Player player = Bukkit.getPlayer(owner);
        if (lore != null) {
            for (String s : lore) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    s = s.replace(entry.getKey(), entry.getValue());
                }
                if (plugin.getPlaceholderAPIHook() != null && plugin.getQuestsConfig().getBoolean("options.gui-use-placeholderapi")) {
                    s = plugin.getPlaceholderAPIHook().replacePlaceholders(player, s);
                }
                newLore.add(s);
            }
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            ism.setDisplayName(ism.getDisplayName().replace(entry.getKey(), entry.getValue()));
        }
        if (plugin.getPlaceholderAPIHook() != null && plugin.getQuestsConfig().getBoolean("options.gui-use-placeholderapi")) {
            ism.setDisplayName(plugin.getPlaceholderAPIHook().replacePlaceholders(player, ism.getDisplayName()));
        }
        ism.setLore(newLore);
        newItemStack.setItemMeta(ism);
        return newItemStack;
    }

    public static void handleMiddleClick(BukkitQuestsPlugin plugin, QMenu menu, Quest quest, Player player, MenuController controller) {
        if (menu.getOwner().hasStartedQuest(quest)) {
            if (!plugin.getQuestsConfig().getBoolean("options.allow-quest-track")) return;

            String tracked = menu.getOwner().getPlayerPreferences().getTrackedQuestId();

            if (quest.getId().equals(tracked)) {
                menu.getOwner().trackQuest(null);
            } else {
                menu.getOwner().trackQuest(quest);
            }
            player.closeInventory();
        }
    }

    public static void handleRightClick(BukkitQuestsPlugin plugin, QMenu menu, Quest quest, Player player, MenuController controller) {
        if (menu.getOwner().hasStartedQuest(quest)) {
            if (!plugin.getQuestsConfig().getBoolean("options.allow-quest-cancel")) return;
            if (plugin.getQuestsConfig().getBoolean("options.gui-confirm-cancel", true)) {
                CancelQMenu cancelQMenu = new CancelQMenu(plugin, menu, menu.getOwner(), quest);
                controller.openMenu(player, cancelQMenu, 1);
            } else {
                if (menu.getOwner().cancelQuest(quest)) {
                    player.closeInventory();
                }
            }
        }
    }

}
