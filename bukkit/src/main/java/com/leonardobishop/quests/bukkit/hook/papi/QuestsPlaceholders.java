package com.leonardobishop.quests.bukkit.hook.papi;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStack;
import com.leonardobishop.quests.bukkit.util.FormatUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QuestsPlaceholders extends PlaceholderExpansion implements Cacheable {

    private final BukkitQuestsPlugin plugin;
    private final Map<String, Map<String, String>> cache = new HashMap<>();
    private final Map<String, SimpleDateFormat> formats = new HashMap<>();

    public QuestsPlaceholders(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void clear() {
        cache.clear();
        formats.clear();
    }

    @Override
    public String getIdentifier() {
        return "quests";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (p == null || !p.isOnline()) return null;
        if (cache.containsKey(p.getName()) && cache.get(p.getName()).containsKey(params))
            return cache.get(p.getName()).get(params);

        String[] args = params.split("_", 4);
        if (args.length < 1) return "Invalid Placeholder";

        final boolean save = args[args.length - 1].toLowerCase().equals("cache");
        if (save) args = Arrays.copyOf(args, args.length - 1);

        final QPlayer qPlayer = plugin.getPlayerManager().getPlayer(p.getUniqueId());
        if (qPlayer == null) return Messages.PLACEHOLDERAPI_DATA_NOT_LOADED.getMessageLegacyColor();
        String split = args[args.length - 1];

        String result;
        if (!args[0].contains(":") && !args[0].equalsIgnoreCase("tracked")) {
            if (args.length > 1 && split.equals(args[1])) split = ",";

            switch (args[0].toLowerCase()) {
                case "all":
                case "a":
                    final List<Quest> listAll = new ArrayList<>(plugin.getQuestManager().getQuestMap().values());
                    result = (args.length == 1 ? String.valueOf(listAll.size()) : parseList(listAll, args[1], split));
                    break;
                case "completed":
                case "c":
                    final List<Quest> listCompleted = qPlayer.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.COMPLETED);
                    listCompleted.removeIf(quest -> !quest.doesCountTowardsCompleted());
                    result = (args.length == 1 ? String.valueOf(listCompleted.size()) : parseList(listCompleted, args[1], split));
                    break;
                case "completedbefore":
                case "cb":
                    final List<Quest> listCompletedB = qPlayer.getQuestProgressFile().getAllQuestsFromProgress(QuestProgressFile.QuestsProgressFilter.COMPLETED_BEFORE);
                    listCompletedB.removeIf(quest -> !quest.doesCountTowardsCompleted());
                    result = (args.length == 1 ? String.valueOf(listCompletedB.size()) : parseList(listCompletedB, args[1], split));
                    break;
                case "started":
                case "s":
                    //TODO cache started quests somewhere, or make a effective started method
                    final List<Quest> listStarted = qPlayer.getEffectiveStartedQuests();
                    result = (args.length == 1 ? String.valueOf(listStarted.size()) : parseList(listStarted, args[1], split));
                    break;
                case "limit":
                case "l":
                    result = String.valueOf(((BukkitQuestsConfig) plugin.getQuestsConfig()).getQuestLimit(p));
                    break;
                case "categories":
                    if (args.length == 1) {
                        result = String.valueOf(plugin.getQuestManager().getCategories().size());
                    } else {
                        final List<String> listCategories = new ArrayList<>();
                        switch (args[1].toLowerCase()) {
                            case "list":
                            case "l":
                                plugin.getQuestManager().getCategories().forEach(c -> {
                                    ItemStack itemStack = plugin.getQItemStackRegistry().getCategoryItemStack(c);
                                    listCategories.add(Chat.legacyStrip(itemStack.getItemMeta().getDisplayName()));
                                });
                                break;
                            case "listid":
                            case "lid":
                                plugin.getQuestManager().getCategories().forEach(c -> listCategories.add(c.getId()));
                                break;
                            default:
                                return args[0] + "_" + args[1] + " is not a valid placeholder";
                        }
                        result = String.join(split, listCategories);
                    }
                    break;
                default:
                    return args[0] + " is not a valid placeholder";
            }
        } else {
            final String[] key = args[0].split(":");
            switch (key[0].toLowerCase()) {
                case "quest":
                case "q":
                case "tracked":
                    if (!key[0].equalsIgnoreCase("tracked") && key.length == 1) return "Please specify quest name";

                    final Quest quest;
                    if (!key[0].equalsIgnoreCase("tracked")) {
                        quest = plugin.getQuestManager().getQuestById(key[1]);
                        if (quest == null) return key[1] + " is not a quest";
                    } else {
                        if (qPlayer.getPlayerPreferences().getTrackedQuestId() == null ||
                                plugin.getQuestManager().getQuestById(qPlayer.getPlayerPreferences().getTrackedQuestId()) == null) {
                            return Messages.PLACEHOLDERAPI_NO_TRACKED_QUEST.getMessageLegacyColor();
                        }
                        quest = plugin.getQuestManager().getQuestById(qPlayer.getPlayerPreferences().getTrackedQuestId());
                    }

                    if (args.length == 1) {
                        result = getQuestDisplayNameStripped(quest);
                    } else {
                        switch (args[1].toLowerCase()) {
                            case "started", "s" -> {
                                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgressOrNull(quest);
                                result = ((questProgress != null && questProgress.isStarted()) ? Messages.PLACEHOLDERAPI_TRUE.getMessageLegacyColor() : Messages.PLACEHOLDERAPI_FALSE.getMessageLegacyColor());
                            }
                            case "starteddate", "sd" -> {
                                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgressOrNull(quest);
                                if (questProgress != null && questProgress.isStarted()) {
                                    result = parseDate(args, questProgress.getStartedDate());
                                } else {
                                    result = "Never";
                                }
                            }
                            case "completed", "c" -> {
                                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgressOrNull(quest);
                                result = ((questProgress != null && questProgress.isCompleted()) ? Messages.PLACEHOLDERAPI_TRUE.getMessageLegacyColor() : Messages.PLACEHOLDERAPI_FALSE.getMessageLegacyColor());
                            }
                            case "completedbefore", "cb" -> {
                                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgressOrNull(quest);
                                result = ((questProgress != null && questProgress.isCompletedBefore()) ? Messages.PLACEHOLDERAPI_TRUE.getMessageLegacyColor() : Messages.PLACEHOLDERAPI_FALSE.getMessageLegacyColor());
                            }
                            case "completiondate", "cd" -> {
                                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgressOrNull(quest);
                                if (questProgress != null && questProgress.isCompleted()) {
                                    result = parseDate(args, questProgress.getCompletionDate());
                                } else {
                                    result = "Never";
                                }
                            }
                            case "cooldown" -> {
                                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgressOrNull(quest);
                                if (questProgress != null && questProgress.isCompleted()) {
                                    final long questCooldown = qPlayer.getQuestProgressFile().getCooldownFor(quest);
                                    if (questCooldown > 0) {
                                        final long questCooldownMillis = TimeUnit.SECONDS.convert(questCooldown, TimeUnit.MILLISECONDS);
                                        result = FormatUtils.time(questCooldownMillis);
                                    } else {
                                        // TODO handle it in a more proper way after storage rework
                                        result = Messages.PLACEHOLDERAPI_NO_COOLDOWN.getMessage();
                                    }
                                } else {
                                    result = "0";
                                }
                            }
                            case "timeleft" -> {
                                if (qPlayer.hasStartedQuest(quest)) {
                                    long timeLeft = qPlayer.getQuestProgressFile().getTimeRemainingFor(quest);
                                    result = timeLeft != -1 ? FormatUtils.time(TimeUnit.SECONDS.convert(timeLeft, TimeUnit.MILLISECONDS)) : Messages.PLACEHOLDERAPI_NO_TIME_LIMIT.getMessage();
                                } else {
                                    result = "0";
                                }
                            }
                            case "canaccept" ->
                                    result = (qPlayer.canStartQuest(quest) == QuestStartResult.QUEST_SUCCESS ? Messages.PLACEHOLDERAPI_TRUE.getMessageLegacyColor() : Messages.PLACEHOLDERAPI_FALSE.getMessageLegacyColor());
                            case "meetsrequirements" ->
                                    result = (qPlayer.getQuestProgressFile().hasMetRequirements(quest) ? Messages.PLACEHOLDERAPI_TRUE.getMessageLegacyColor() : Messages.PLACEHOLDERAPI_FALSE.getMessageLegacyColor());
                            default -> {
                                if (!args[1].contains(":"))
                                    return args[0] + "_" + args[1] + " is not a valid placeholder";

                                final String[] t = args[1].split(":");
                                if (t[0].equalsIgnoreCase("task") || t[0].equalsIgnoreCase("t")) {
                                    if (t.length == 1) return "Please specify task name";

                                    QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgressOrNull(quest);

                                    if (args.length == 2) {
                                        result = t[1];
                                    } else {
                                        switch (args[2].toLowerCase()) {
                                            case "progress":
                                            case "p":
                                                final Object progress = questProgress != null ? questProgress.getTaskProgress(t[1]).getProgress() : null;
                                                result = (progress == null ? "0" : String.valueOf(progress));
                                                break;
                                            case "completed":
                                            case "c":
                                                final boolean completed = questProgress != null && questProgress.getTaskProgress(t[1]).isCompleted();
                                                result = String.valueOf(completed ? Messages.PLACEHOLDERAPI_TRUE.getMessageLegacyColor() : Messages.PLACEHOLDERAPI_FALSE.getMessageLegacyColor());
                                                break;
                                            case "goal":
                                            case "g":
                                                final Task task = quest.getTaskById(t[1]);
                                                result = (task != null ? String.valueOf(plugin.getTaskTypeManager().getTaskType(task.getType()).getGoal(task)) : "0");
                                                break;
                                            default:
                                                return args[0] + "_" + args[1] + "_" + args[2] + " is not a valid placeholder";
                                        }
                                    }
                                } else if (t[0].equalsIgnoreCase("placeholder") || t[0].equalsIgnoreCase("p")) {
                                    if (t.length == 1) return "Please specify placeholder name";

                                    String placeholder = quest.getPlaceholders().get(t[1]);
                                    if (placeholder == null) {
                                        return t[1] + " is not a valid placeholder within quest " + quest.getId();
                                    }

                                    QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgressOrNull(quest);
                                    assert questProgress != null;

                                    placeholder = QItemStack.processPlaceholders(plugin, Chat.legacyColor(placeholder), questProgress);
                                    return placeholder;
                                } else {
                                    return args[0] + "_" + args[1] + " is not a valid placeholder";
                                }
                            }
                        }
                    }
                    break;
                case "category":
                case "c":
                    if (!plugin.getQuestsConfig().getBoolean("options.categories-enabled")) return "Categories Disabled";
                    if (key.length == 1) return "Please specify category name";

                    final Category category = plugin.getQuestManager().getCategoryById(key[1]);
                    if (category == null) return key[1] + " is not a category";

                    if (args.length == 1) {
                        ItemStack itemStack = plugin.getQItemStackRegistry().getCategoryItemStack(category);
                        result = Chat.legacyStrip(itemStack.getItemMeta().getDisplayName());
                    } else {
                        if (args.length > 2 && split.equals(args[2])) split = ",";
                        switch (args[1].toLowerCase()) {
                            case "all":
                            case "a":
                                final List<Quest> listAll = getCategoryQuests(qPlayer, category, QuestProgressFile.QuestsProgressFilter.ALL);
                                result = (args.length == 2 ? String.valueOf(listAll.size()) : parseList(listAll, args[2], split));
                                break;
                            case "completed":
                            case "c":
                                final List<Quest> listCompleted = getCategoryQuests(qPlayer, category, QuestProgressFile.QuestsProgressFilter.COMPLETED);
                                result = (args.length == 2 ? String.valueOf(listCompleted.size()) : parseList(listCompleted, args[2], split));
                                break;
                            case "completedbefore":
                            case "cb":
                                final List<Quest> listCompletedB = getCategoryQuests(qPlayer, category, QuestProgressFile.QuestsProgressFilter.COMPLETED_BEFORE);
                                result = (args.length == 2 ? String.valueOf(listCompletedB.size()) : parseList(listCompletedB, args[2], split));
                                break;
                            case "started":
                            case "s":
                                final List<Quest> listStarted = getCategoryQuests(qPlayer, category, QuestProgressFile.QuestsProgressFilter.STARTED);
                                result = (args.length == 2 ? String.valueOf(listStarted.size()) : parseList(listStarted, args[2], split));
                                break;
                            default:
                                return args[0] + "_" + args[1] + " is not a valid placeholder";
                        }
                    }
                    break;
                default:
                    return args[0] + " is not a valid placeholder";
            }
        }
        return (save ? cache(p.getName(), params, result) : result);
    }

    private String cache(String player, String params, String result) {
        if (!cache.containsKey(player) || !cache.get(player).containsKey(params)) {
            final Map<String, String> map = new HashMap<>();
            map.put(params, result);
            cache.put(player, map);
            plugin.getScheduler().runTaskLaterAsynchronously(() -> cache.get(player).remove(params), plugin.getConfig().getInt("options.placeholder-cache-time", 10) * 20L);
        }
        return result;
    }

    private String parseDate(String[] args, Long date) {
        final String format = (args[args.length - 1].equals(args[1]) ? "dd/MM/yyyy" : args[args.length - 1]);
        SimpleDateFormat sdf;
        if (formats.containsKey(format)) {
            sdf = formats.get(format);
        } else {
            sdf = new SimpleDateFormat(format);
            formats.put(format, sdf);
        }
        return sdf.format(date);
    }

    private String parseList(List<Quest> list, String type, String separator) {
        final List<String> quests = new ArrayList<>();
        switch (type.toLowerCase()) {
            case "list":
            case "l":
                list.forEach(q -> quests.add(getQuestDisplayNameStripped(q)));
                break;
            case "listid":
            case "lid":
                list.forEach(q -> quests.add(q.getId()));
                break;
            default:
                return type + "is not a valid placeholder";
        }
        return String.join(separator, quests);
    }

    private String getQuestDisplayNameStripped(Quest quest) {
        QItemStack qItemStack = plugin.getQItemStackRegistry().getQuestItemStack(quest);
        if (qItemStack != null) return Chat.legacyStrip(qItemStack.getName());
        return null;
    }

    private List<Quest> getCategoryQuests(QPlayer questP, Category category, QuestProgressFile.QuestsProgressFilter filter) {
        final List<Quest> categoryQuests = new ArrayList<>();
        category.getRegisteredQuestIds().forEach(q -> {
            Quest quest = plugin.getQuestManager().getQuestById(q);
            if (quest != null) {
                switch (filter) {
                    case STARTED -> {
                        QuestProgress qp = questP.getQuestProgressFile().getQuestProgressOrNull(quest);
                        if (qp != null && qp.isStarted()) {
                            categoryQuests.add(quest);
                        }
                    }
                    case COMPLETED -> {
                        QuestProgress qp = questP.getQuestProgressFile().getQuestProgressOrNull(quest);
                        if (qp != null && qp.isCompleted()) {
                            categoryQuests.add(quest);
                        }
                    }
                    case COMPLETED_BEFORE -> {
                        QuestProgress qp = questP.getQuestProgressFile().getQuestProgressOrNull(quest);
                        if (qp != null && qp.isCompletedBefore()) {
                            categoryQuests.add(quest);
                        }
                    }
                    default -> categoryQuests.add(quest);
                }
            }
        });
        return categoryQuests;
    }
}
