package com.leonardobishop.quests.obj.misc;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.quests.Quest;
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

    private final Quests plugin;

    private String name;
    private List<String> loreNormal;
    private List<String> loreStarted;
    private ItemStack startingItemStack;

    public QItemStack(Quests plugin, String name, List<String> loreNormal, List<String> loreStarted, ItemStack startingItemStack) {
        this.plugin = plugin;
        this.name = name;
        this.loreNormal = loreNormal;
        this.loreStarted = loreStarted;
        this.startingItemStack = startingItemStack;
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
    public ItemStack toItemStack(Quest quest, QuestProgressFile questProgressFile, QuestProgress questProgress) {
        ItemStack is = new ItemStack(startingItemStack);
        ItemMeta ism = is.getItemMeta();
        ism.setDisplayName(name);
        List<String> formattedLore = new ArrayList<>();
        List<String> tempLore = new ArrayList<>(loreNormal);

        Player player = Bukkit.getPlayer(questProgressFile.getPlayerUUID());
        if (questProgressFile.hasStartedQuest(quest)) {
            tempLore.addAll(loreStarted);
            ism.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            try {
                ism.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                ism.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            } catch (Exception ignored) {

            }
        }
        if (plugin.getPlaceholderAPIHook() != null && Options.GUI_USE_PLACEHOLDERAPI.getBooleanValue()) {
            ism.setDisplayName(plugin.getPlaceholderAPIHook().replacePlaceholders(player, ism.getDisplayName()));
        }
        if (questProgress != null) {
            for (String s : tempLore) {
                Matcher m = Pattern.compile("\\{([^}]+)}").matcher(s);
                while (m.find()) {
                    String[] parts = m.group(1).split(":");
                    if (parts.length > 1) {
                        if (questProgress.getTaskProgress(parts[0]) == null) {
                            continue;
                        }
                        if (parts[1].equals("progress")) {
                            String str = String.valueOf(questProgress.getTaskProgress(parts[0]).getProgress());
                            s = s.replace("{" + m.group(1) + "}", (str.equals("null") ? String.valueOf(0) : str));
                        }
                        if (parts[1].equals("complete")) {
                            String str = String.valueOf(questProgress.getTaskProgress(parts[0]).isCompleted());
                            s = s.replace("{" + m.group(1) + "}", str);
                        }
                    }
                }
                if (plugin.getPlaceholderAPIHook() != null && Options.GUI_USE_PLACEHOLDERAPI.getBooleanValue()) {
                    s = plugin.getPlaceholderAPIHook().replacePlaceholders(player, s);
                }
                formattedLore.add(s);
            }
        }
        ism.setLore(formattedLore);
        is.setItemMeta(ism);
        return is;
    }
}
