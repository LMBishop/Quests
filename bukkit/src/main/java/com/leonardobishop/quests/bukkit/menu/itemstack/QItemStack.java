package com.leonardobishop.quests.bukkit.menu.itemstack;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QItemStack {

    private final BukkitQuestsPlugin plugin;

    private String name;
    private List<String> loreNormal;
    private List<String> loreStarted;
    private final List<String> globalLoreAppendNormal;
    private final List<String> globalLoreAppendNotStarted;
    private final List<String> globalLoreAppendStarted;
    private final List<String> globalLoreAppendTracked;
    private ItemStack startingItemStack;

    public QItemStack(BukkitQuestsPlugin plugin, String name, List<String> loreNormal, List<String> loreStarted, ItemStack startingItemStack) {
        this.plugin = plugin;
        this.name = name;
        this.loreNormal = loreNormal;
        this.loreStarted = loreStarted;
        this.startingItemStack = startingItemStack;

        this.globalLoreAppendNormal = Chat.legacyColor(plugin.getQuestsConfig().getStringList("global-quest-display.lore.append-normal"));
        this.globalLoreAppendNotStarted = Chat.legacyColor(plugin.getQuestsConfig().getStringList("global-quest-display.lore.append-not-started"));
        this.globalLoreAppendStarted = Chat.legacyColor(plugin.getQuestsConfig().getStringList("global-quest-display.lore.append-started"));
        this.globalLoreAppendTracked = Chat.legacyColor(plugin.getQuestsConfig().getStringList("global-quest-display.lore.append-tracked"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLoreNormal() {
        return loreNormal;
    }

    public void setLoreNormal(List<String> loreNormal) {
        this.loreNormal = loreNormal;
    }

    public List<String> getLoreStarted() {
        return loreStarted;
    }

    public void setLoreStarted(List<String> loreStarted) {
        this.loreStarted = loreStarted;
    }

    public ItemStack getStartingItemStack() {
        return startingItemStack;
    }

    public void setStartingItemStack(ItemStack startingItemStack) {
        this.startingItemStack = startingItemStack;
    }

    @SuppressWarnings("deprecation")
    public ItemStack toItemStack(Quest quest, QPlayer qPlayer, QuestProgress questProgress) {
        ItemStack is = new ItemStack(startingItemStack);
        ItemMeta ism = is.getItemMeta();
        ism.setDisplayName(name);
        List<String> formattedLore = new ArrayList<>();
        List<String> tempLore = new ArrayList<>();

        if (!plugin.getQuestsConfig().getBoolean("options.global-task-configuration-override") || globalLoreAppendNormal.isEmpty()) {
            tempLore.addAll(loreNormal);
        }
        tempLore.addAll(globalLoreAppendNormal);

        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (qPlayer.hasStartedQuest(quest)) {
            boolean tracked = quest.getId().equals(qPlayer.getPlayerPreferences().getTrackedQuestId());
            if (!plugin.getQuestsConfig().getBoolean("options.global-task-configuration-override")|| globalLoreAppendStarted.isEmpty()) {
                tempLore.addAll(loreStarted);
            }
            if (tracked) {
                tempLore.addAll(globalLoreAppendTracked);
            } else {
                tempLore.addAll(globalLoreAppendStarted);
            }
            ism.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            try {
                ism.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                ism.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            } catch (Exception ignored) { }
        } else {
            tempLore.addAll(globalLoreAppendNotStarted);
        }
        if (plugin.getQuestsConfig().getBoolean("options.gui-use-placeholderapi")) {
            ism.setDisplayName(plugin.getPlaceholderAPIProcessor().apply(player, ism.getDisplayName()));
        }
        if (questProgress != null) {
            for (String s : tempLore) {
                s = processPlaceholders(s, questProgress);
                if (plugin.getQuestsConfig().getBoolean("options.gui-use-placeholderapi")) {
                    s = plugin.getPlaceholderAPIProcessor().apply(player, s);
                }
                formattedLore.add(s);
            }
        }
        ism.setLore(formattedLore);
        is.setItemMeta(ism);
        return is;
    }

    public static String processPlaceholders(String s, QuestProgress questProgress) {
        Matcher m = Pattern.compile("\\{([^}]+)}").matcher(s);
        while (m.find()) {
            String[] parts = m.group(1).split(":");
            if (parts.length > 1) {
                if (questProgress.getTaskProgress(parts[0]) == null) {
                    continue;
                }
                if (parts[1].equals("progress")) {
                    Object progress = questProgress.getTaskProgress(parts[0]).getProgress();
                    String str;
                    if (progress instanceof Float || progress instanceof Double) {
                        str = String.format(String.valueOf(progress), "%.2f");
                    } else {
                        str = String.valueOf(progress);
                    }

                    s = s.replace("{" + m.group(1) + "}", (progress == null ? String.valueOf(0) : str));
                }
                if (parts[1].equals("complete")) {
                    String str;
                    if (questProgress.getTaskProgress(parts[0]).isCompleted()) {
                        str = Chat.legacyColor(Messages.UI_PLACEHOLDERS_TRUE.getMessageLegacyColor());
                    } else {
                        str = Chat.legacyColor(Messages.UI_PLACEHOLDERS_FALSE.getMessageLegacyColor());
                    }
                    s = s.replace("{" + m.group(1) + "}", str);
                }
            }
        }
        return s;
    }
}
