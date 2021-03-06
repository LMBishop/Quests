package com.leonardobishop.quests.menu;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.events.MenuController;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuUtil {

    public static ItemStack applyPlaceholders(Quests plugin, UUID owner, ItemStack is) {
        return applyPlaceholders(plugin, owner, is, Collections.emptyMap());
    }

    public static ItemStack applyPlaceholders(Quests plugin, UUID owner, ItemStack is, Map<String, String> placeholders) {
        ItemStack newItemStack = is.clone();
        List<String> lore = newItemStack.getItemMeta().getLore();
        List<String> newLore = new ArrayList<>();
        ItemMeta ism = newItemStack.getItemMeta();
        Player player = Bukkit.getPlayer(owner);
        if (lore != null) {
            for (String s : lore) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    s = s.replace(entry.getKey(), entry.getValue());
                    if (plugin.getPlaceholderAPIHook() != null && Options.GUI_USE_PLACEHOLDERAPI.getBooleanValue()) {
                        s = plugin.getPlaceholderAPIHook().replacePlaceholders(player, s);
                    }
                }
                newLore.add(s);
            }
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            ism.setDisplayName(ism.getDisplayName().replace(entry.getKey(), entry.getValue()));
            if (plugin.getPlaceholderAPIHook() != null && Options.GUI_USE_PLACEHOLDERAPI.getBooleanValue()) {
                ism.setDisplayName(plugin.getPlaceholderAPIHook().replacePlaceholders(player, ism.getDisplayName()));
            }
        }
        ism.setLore(newLore);
        newItemStack.setItemMeta(ism);
        return newItemStack;
    }

    public static void handleMiddleClick(QMenu menu, Quest quest, Player player, MenuController controller) {
        if (menu.getOwner().getQuestProgressFile().hasStartedQuest(quest)) {
            String tracked = menu.getOwner().getQuestProgressFile().getPlayerPreferences().getTrackedQuestId();

            if (quest.getId().equals(tracked)) {
                menu.getOwner().getQuestProgressFile().trackQuest(null);
            } else {
                menu.getOwner().getQuestProgressFile().trackQuest(quest);
            }
            player.closeInventory();
        }
    }

    public static void handleRightClick(QMenu menu, Quest quest, Player player, MenuController controller) {
        if (menu.getOwner().getQuestProgressFile().hasStartedQuest(quest)) {
            if (Options.QUEST_AUTOSTART.getBooleanValue()) return;
            CancelQMenu cancelQMenu = new CancelQMenu(menu.getOwner(), menu, quest);
            controller.openMenu(player, cancelQMenu, 1);
        }
    }

}
