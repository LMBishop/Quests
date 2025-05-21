package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.CategoryQMenu;
import com.leonardobishop.quests.bukkit.menu.QuestQMenu;
import com.leonardobishop.quests.bukkit.menu.QuestSortWrapper;
import com.leonardobishop.quests.bukkit.menu.StartedQMenu;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MenuUtils {

    public static ItemStack applyPlaceholders(@Nullable BukkitQuestsPlugin plugin, @Nullable UUID owner, ItemStack is) {
        return applyPlaceholders(plugin, owner, is, Collections.emptyMap());
    }

    public static ItemStack applyPlaceholders(@Nullable BukkitQuestsPlugin plugin, @Nullable UUID owner, ItemStack is, Map<String, String> placeholders) {
        ItemStack newItemStack = is.clone();
        List<String> lore = newItemStack.getItemMeta().getLore();
        List<String> newLore = new ArrayList<>();
        ItemMeta ism = newItemStack.getItemMeta();
        Player player = owner == null ? null : Bukkit.getPlayer(owner);
        boolean usePAPI = player != null && plugin != null && plugin.getQuestsConfig().getBoolean("options.gui-use-placeholderapi");
        if (lore != null) {
            for (String s : lore) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    s = s.replace(entry.getKey(), entry.getValue());
                }
                if (usePAPI) {
                    s = plugin.getPlaceholderAPIProcessor().apply(player, s);
                }
                newLore.add(s);
            }
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            ism.setDisplayName(ism.getDisplayName().replace(entry.getKey(), entry.getValue()));
        }
        if (usePAPI) {
            ism.setDisplayName(plugin.getPlaceholderAPIProcessor().apply(player, ism.getDisplayName()));
        }
        ism.setLore(newLore);
        newItemStack.setItemMeta(ism);
        return newItemStack;
    }

    /**
     * Open the main menu for the player
     *
     * @param qPlayer player
     */
    public static void openMainMenu(BukkitQuestsPlugin plugin, QPlayer qPlayer) {
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (player == null) {
            return;
        }

        if (plugin.getQuestsConfig().getBoolean("options.categories-enabled")) {
            CategoryQMenu categoryQMenu = new CategoryQMenu(plugin, qPlayer);
            plugin.getMenuController().openMenu(player, categoryQMenu);
        } else {
            List<Quest> quests = new ArrayList<>();
            for (Map.Entry<String, Quest> entry : plugin.getQuestManager().getQuestMap().entrySet()) {
                quests.add(entry.getValue());
            }
            Collections.sort(quests);
            QuestQMenu questQMenu = new QuestQMenu(plugin, qPlayer, quests, null, null);
            plugin.getMenuController().openMenu(player, questQMenu);
        }
//        } else {
//            DailyQMenu dailyQMenu = new DailyQMenu(plugin, this);
//            dailyQMenu.populate();
//            plugin.getMenuController().openMenu(player, dailyQMenu, 1);
//        }
    }

    public static void openQuestCategory(BukkitQuestsPlugin plugin, QPlayer qPlayer, Category category, CategoryQMenu superMenu) {
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (player == null) {
            return;
        }

        List<Quest> quests = new ArrayList<>();
        for (String questid : category.getRegisteredQuestIds()) {
            Quest quest = plugin.getQuestManager().getQuestById(questid);
            if (quest != null) {
                quests.add(quest);
            }
        }
        Collections.sort(quests);
        QuestQMenu questQMenu = new QuestQMenu(plugin, qPlayer, quests, category, superMenu);
        plugin.getMenuController().openMenu(player, questQMenu);
    }

    /**
     * Open the started menu for the player
     *
     * @param qPlayer player
     */
    public static void openStartedQuests(BukkitQuestsPlugin plugin, QPlayer qPlayer) {
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (player == null) {
            return;
        }

        List<QuestSortWrapper> quests = new ArrayList<>();
        for (Map.Entry<String, Quest> entry : plugin.getQuestManager().getQuestMap().entrySet()) {
            quests.add(new QuestSortWrapper(plugin, entry.getValue()));
        }
        Collections.sort(quests);

        StartedQMenu startedQMenu = new StartedQMenu(plugin, qPlayer, quests.stream().map(QuestSortWrapper::getQuest).collect(Collectors.toList()));

        plugin.getMenuController().openMenu(player, startedQMenu);
    }

    public static ClickType getClickType(BukkitQuestsConfig config, String path, String def) {
        String value = config.getString(path, def);
        try {
            return ClickType.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }


    public static Map<String, String> fillPagePlaceholders(int page) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{prevpage}", String.valueOf(page - 1));
        placeholders.put("{nextpage}", String.valueOf(page + 1));
        placeholders.put("{page}", String.valueOf(page));

        return placeholders;
    }

    public static int getHigherOrEqualMultiple(int num, int base) {
        int r = num % base;
        return r == 0 ? num : num + base - r;
    }

}
