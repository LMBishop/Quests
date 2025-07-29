package com.leonardobishop.quests.bukkit.menu.itemstack;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.FormatUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.tasktype.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
            ism.addEnchant(Enchantment.KNOCKBACK, 1, true);
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
                s = processPlaceholders(plugin, s, questProgress);
                s = processTimeLeft(s, quest, qPlayer.getQuestProgressFile());
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

    public static final Pattern TASK_PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+):(progress|goal|complete|id)}");

    public static String processPlaceholders(BukkitQuestsPlugin plugin, String s, QuestProgress questProgress) {
        return processPlaceholders(plugin, s, questProgress, null);
    }

    public static String processPlaceholders(BukkitQuestsPlugin plugin, String s, QuestProgress questProgress, TaskProgress taskProgress) {
        Matcher matcher = TASK_PLACEHOLDER_PATTERN.matcher(s);

        while (matcher.find()) {
            TaskProgress matchedTaskProgress;

            String taskIdPart = matcher.group(1);
            if (taskProgress != null && taskIdPart.equals("this")) {
                matchedTaskProgress = taskProgress;
            } else {
                matchedTaskProgress = questProgress.getTaskProgressOrNull(taskIdPart);
            }

            if (matchedTaskProgress == null) {
                continue;
            }

            String placeholderPart = matcher.group(2);
            String replacement;

            switch (placeholderPart) {
                // formatted progress placeholders
                case "progress" -> {
                    Object progress = matchedTaskProgress.getProgress();
                    if (progress instanceof Float || progress instanceof Double || progress instanceof BigDecimal) {
                        replacement = FormatUtils.floating((Number) progress);
                    } else if (progress instanceof Integer || progress instanceof Long || progress instanceof BigInteger) {
                        replacement = FormatUtils.integral((Number) progress);
                    } else if (progress != null) {
                        replacement = String.valueOf(progress);
                    } else {
                        replacement = String.valueOf(0);
                    }
                }

                // goal placeholders
                case "goal" -> {
                    Quest quest = plugin.getQuestManager().getQuestById(questProgress.getQuestId());
                    Task task = quest != null ? quest.getTaskById(matchedTaskProgress.getTaskId()) : null;
                    TaskType taskType = task != null ? plugin.getTaskTypeManager().getTaskType(task.getType()) : null;
                    Object goal = taskType != null ? taskType.getGoal(task) : null;
                    if (goal instanceof Float || goal instanceof Double || goal instanceof BigDecimal) {
                        replacement = FormatUtils.floating((Number) goal);
                    } else if (goal instanceof Integer || goal instanceof Long || goal instanceof BigInteger) {
                        replacement = FormatUtils.integral((Number) goal);
                    } else if (goal != null) {
                        replacement = String.valueOf(goal);
                    } else {
                        replacement = String.valueOf(0);
                    }
                }

                // completion placeholders
                case "complete" -> {
                    boolean completed = matchedTaskProgress.isCompleted();
                    if (completed) {
                        replacement = Messages.UI_PLACEHOLDERS_TRUE.getMessageLegacyColor();
                    } else {
                        replacement = Messages.UI_PLACEHOLDERS_FALSE.getMessageLegacyColor();
                    }
                }

                // may be particularly useful when using PAPI placeholders in boss bars
                case "id" -> replacement = matchedTaskProgress.getTaskId();

                // set it to null so we can check if there is need to update the matcher
                default -> replacement = null;
            }

            // update the matcher only if something needs to be replaced
            if (replacement != null) {
                s = s.substring(0, matcher.start()) + replacement + s.substring(matcher.end());
                matcher = TASK_PLACEHOLDER_PATTERN.matcher(s);
            }
        }

        return s;
    }

    public static String processTimeLeft(String s, Quest quest, QuestProgressFile questProgressFile) {
        String timeLeft;
        if (quest.isTimeLimitEnabled()) {
            timeLeft = FormatUtils.time(TimeUnit.SECONDS.convert(questProgressFile.getTimeRemainingFor(quest), TimeUnit.MILLISECONDS));
        } else {
            timeLeft = Chat.legacyColor(Messages.UI_PLACEHOLDERS_NO_TIME_LIMIT.getMessageLegacyColor());
        }
        return s.replace("{timeleft}", timeLeft);
    }
}
