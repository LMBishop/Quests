package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.quest.Category;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabHelper {

    static BukkitQuestsPlugin plugin;

    static {
        plugin = BukkitQuestsPlugin.getPlugin(BukkitQuestsPlugin.class);
    }

    public static List<String> matchTabComplete(String arg, List<String> options) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, options, completions);
        Collections.sort(completions);
        return completions;
    }

    public static List<String> tabCompleteCategory(String arg) {
        List<String> options = new ArrayList<>();
        for (Category c : plugin.getQuestManager().getCategories()) {
            options.add(c.getId());
        }
        return matchTabComplete(arg, options);
    }

    public static List<String> tabCompleteQuests(String arg) {
        List<String> options = new ArrayList<>(plugin.getQuestManager().getQuestMap().keySet());
        return matchTabComplete(arg, options);
    }

    public static List<String> tabCompleteQuestsOrWildcard(String arg) {
        List<String> options = new ArrayList<>(plugin.getQuestManager().getQuestMap().keySet());
        options.add("*");
        return matchTabComplete(arg, options);
    }

}
