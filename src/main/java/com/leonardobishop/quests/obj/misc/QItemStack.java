package com.leonardobishop.quests.obj.misc;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.quests.Quest;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QItemStack {

    private String name;
    private List<String> loreNormal;
    private List<String> loreStarted;
    private Material type;
    private int data;

    public QItemStack(String name, List<String> loreNormal, List<String> loreStarted, Material type, int data) {
        this.name = name;
        this.loreNormal = loreNormal;
        this.loreStarted = loreStarted;
        this.type = type;
        this.data = data;
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

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @SuppressWarnings("deprecation")
    public ItemStack toItemStack(Quest quest, QuestProgressFile questProgressFile, QuestProgress questProgress) {
        ItemStack is = new ItemStack(type, 1, (short) data);
        ItemMeta ism = is.getItemMeta();
        ism.setDisplayName(name);
        List<String> formattedLore = new ArrayList<>();
        List<String> tempLore = new ArrayList<>(loreNormal);
        if (questProgressFile.hasStartedQuest(quest)) {
            tempLore.addAll(loreStarted);
            ism.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            try {
                ism.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                ism.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            } catch (Exception ignored) {

            }
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
                formattedLore.add(s);
            }
        }
        ism.setLore(formattedLore);
        is.setItemMeta(ism);
        return is;
    }
}
