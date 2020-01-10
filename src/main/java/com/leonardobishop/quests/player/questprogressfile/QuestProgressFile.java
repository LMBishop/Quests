package com.leonardobishop.quests.player.questprogressfile;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.obj.Messages;
import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class QuestProgressFile {

    private final Map<String, QuestProgress> questProgress = new HashMap<>();
    private final UUID playerUUID; //renamed for better reading
    private final Quests plugin;

    public QuestProgressFile(UUID player, Quests plugin) {
        this.playerUUID = player;
        this.plugin = plugin;
    }

    //TODO change back to quest id to save performance

    public boolean completeQuest(Quest quest) {
        QuestProgress questProgress = getQuestProgress(quest);
        questProgress.setStarted(false);
        questProgress.setCompleted(true);
        questProgress.setCompletedBefore(true);
        questProgress.setCompletionDate(System.currentTimeMillis());
        Optional<Player> optionalPlayer = Optional.ofNullable(Bukkit.getPlayer(this.playerUUID));
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            Bukkit.getServer().getScheduler().runTask(plugin, () -> {
                for (String s : quest.getRewards()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replace("{player}", player.getName()));
                }
            });
            player.sendMessage(Messages.QUEST_COMPLETE.getMessage().replace("{quest}", quest.getDisplayNameStripped()));
            if (Options.TITLES_ENABLED.getBooleanValue()) {
                plugin.getTitle().sendTitle(player, Messages.TITLE_QUEST_COMPLETE_TITLE.getMessage().replace("{quest}", quest
                        .getDisplayNameStripped()), Messages.TITLE_QUEST_COMPLETE_SUBTITLE.getMessage().replace("{quest}", quest
                        .getDisplayNameStripped()));
            }
            for (String s : quest.getRewardString()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        return true;
    }

    /**
     * Check if the player can start a quest.
     * <p>
     * Warning: will fail if the player is not online.
     *
     * @param quest the quest to check
     * @return 0 if true, 1 if limit reached, 2 if quest is already completed, 3 if quest has cooldown, 4 if still locked, 5 if already started, 6 if
     * no permission, 7 if no permission for category
     */
    public int canStartQuest(Quest quest) {
        Player p = Bukkit.getPlayer(playerUUID);
        if (getStartedQuests().size() >= Options.QUESTS_START_LIMIT.getIntValue()) {
            return 1;
        }
        QuestProgress questProgress = getQuestProgress(quest);
        if (!quest.isRepeatable() && questProgress.isCompletedBefore()) {

            return 2;
        }
        long cooldown = getCooldownFor(quest);
        if (cooldown > 0) {
            return 3;
        }
        if (!hasMetRequirements(quest)) {
            return 4;
        }
        if (questProgress.isStarted()) {
            return 5;
        }
        if (quest.isPermissionRequired()) {
            if (p != null) { //Wrong usage for this.player, you use p in method bellow
                if (!p.hasPermission("quests.quest." + quest.getId())) {
                    return 6;
                }
            } else {
                return 6;
            }
        }
        if (quest.getCategoryId() != null && plugin.getQuestManager().getCategoryById(quest.getCategoryId()) != null && plugin.getQuestManager()
                .getCategoryById(quest.getCategoryId()).isPermissionRequired()) {
            if (p != null) { //Wrong usage for this.player, you use p in method bellow
                if (!p.hasPermission("quests.category." + quest.getCategoryId())) {
                    return 7;
                }
            } else {
                return 7;
            }
        }
        return 0;
    }

    /**
     * Start a quest for the player.
     * <p>
     * Warning: will fail if the player is not online.
     *
     * @param quest the quest to check
     * @return 0 if successful, 1 if limit reached, 2 if quest is already completed, 3 if quest has cooldown, 4 if still locked, 5 if already started, 6 if
     * no permission, 7 if no permission for category, 8 if other
     */
    public int startQuest(Quest quest) {
        Player p = Bukkit.getPlayer(playerUUID);
        int code = canStartQuest(quest);
        if (p != null) {
            switch (code) {
                case 0:
                    break;
                case 1:
                    p.sendMessage(Messages.QUEST_START_LIMIT.getMessage().replace("{limit}", String.valueOf(Options.QUESTS_START_LIMIT.getIntValue())));
                    break;
                case 2:
                    p.sendMessage(Messages.QUEST_START_DISABLED.getMessage());
                    break;
                case 3:
                    long cooldown = getCooldownFor(quest);
                    p.sendMessage(Messages.QUEST_START_COOLDOWN.getMessage().replace("{time}", String.valueOf(plugin.convertToFormat(TimeUnit.MINUTES.convert
                            (cooldown, TimeUnit.MILLISECONDS)))));
                    break;
                case 4:
                    p.sendMessage(Messages.QUEST_START_LOCKED.getMessage());
                    break;
                case 5:
                    p.sendMessage(Messages.QUEST_START_STARTED.getMessage());
                    break;
                case 6:
                    p.sendMessage(Messages.QUEST_START_PERMISSION.getMessage());
                    break;
                case 7:
                    p.sendMessage(Messages.QUEST_CATEGORY_QUEST_PERMISSION.getMessage());
                    break;
            }
        }
        if (code == 0) {
            QuestProgress questProgress = getQuestProgress(quest);
            questProgress.setStarted(true);
            for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
                taskProgress.setCompleted(false);
                taskProgress.setProgress(null);
            }
            questProgress.setCompleted(false);
            Optional<Player> optionalPlayer = Optional.ofNullable(Bukkit.getPlayer(getPlayerUUID()));
            if (optionalPlayer.isPresent()) {
                Player player = optionalPlayer.get();
                player.sendMessage(Messages.QUEST_START.getMessage().replace("{quest}", quest.getDisplayNameStripped()));
                if (Options.TITLES_ENABLED.getBooleanValue()) {
                    plugin.getTitle().sendTitle(player, Messages.TITLE_QUEST_START_TITLE.getMessage().replace("{quest}", quest
                            .getDisplayNameStripped()), Messages.TITLE_QUEST_START_SUBTITLE.getMessage().replace("{quest}", quest
                            .getDisplayNameStripped()));
                }
                for (String s : quest.getStartString()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
            }
        }
        return code;
    }

    public boolean cancelQuest(Quest quest) {
        QuestProgress questProgress = getQuestProgress(quest);
        Optional<Player> optionalPlayer = Optional.ofNullable(Bukkit.getPlayer(playerUUID));
        if (!questProgress.isStarted()) {
            optionalPlayer.ifPresent(player -> player.sendMessage(Messages.QUEST_CANCEL_NOTSTARTED.getMessage()));
            return false;
        }
        questProgress.setStarted(false);
        for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
            taskProgress.setProgress(null);
        }
        optionalPlayer.ifPresent(player -> player.sendMessage(Messages.QUEST_CANCEL.getMessage().replace("{quest}", quest.getDisplayNameStripped())));
        return true;
    }

    public void addQuestProgress(QuestProgress questProgress) {
        this.questProgress.put(questProgress.getQuestId(), questProgress);
    }

    public List<Quest> getStartedQuests() {
        List<Quest> startedQuests = new ArrayList<>();
        for (QuestProgress questProgress : questProgress.values()) {
            if (questProgress.isStarted()) {
                startedQuests.add(plugin.getQuestManager().getQuestById(questProgress.getQuestId()));
            }
        }
        return startedQuests;
    }

    public boolean hasQuestProgress(Quest quest) {
        return questProgress.containsKey(quest.getId());
    }

    public boolean hasStartedQuest(Quest quest) {
        if (!Options.QUEST_AUTOSTART.getBooleanValue()) {
            return hasQuestProgress(quest) && getQuestProgress(quest).isStarted();
        } else {
            int response = canStartQuest(quest);
            return response == 0 || response == 5;
        }

    }

    public long getCooldownFor(Quest quest) {
        QuestProgress questProgress = getQuestProgress(quest);
        if (quest.isCooldownEnabled() && questProgress.isCompleted()) {
            if (questProgress.getCompletionDate() > 0) {
                long date = questProgress.getCompletionDate();
                return (date + TimeUnit.MILLISECONDS.convert(quest.getCooldown(), TimeUnit.MINUTES)) - System.currentTimeMillis();
            }
        }
        return 0;
    }

    public boolean hasMetRequirements(Quest quest) {
        for (String id : quest.getRequirements()) {
            Quest q = plugin.getQuestManager().getQuestById(id);
            if (q == null) {
                continue;
            }
            if (hasQuestProgress(q) && !getQuestProgress(q).isCompletedBefore()) {
                return false;
            } else if (!hasQuestProgress(q)) {
                return false;
            }
        }
        return true;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public QuestProgress getQuestProgress(Quest quest) {
        if (questProgress.containsKey(quest.getId())) {
            return questProgress.get(quest.getId());
        } else if (generateBlankQuestProgress(quest.getId())) {
            return getQuestProgress(quest);
        }
        return null;
    }

    public boolean generateBlankQuestProgress(String questid) {
        if (plugin.getQuestManager().getQuestById(questid) != null) {
            Quest quest = plugin.getQuestManager().getQuestById(questid);
            QuestProgress questProgress = new QuestProgress(quest.getId(), false, false, 0, playerUUID, false, false);
            for (Task task : quest.getTasks()) {
                TaskProgress taskProgress = new TaskProgress(task.getId(), null, playerUUID, false, false);
                questProgress.addTaskProgress(taskProgress);
            }

            addQuestProgress(questProgress);
            return true;
        }
        return false;
    }

    public void saveToDisk(boolean disable) {
        File file = null;
        try {
            File directory = new File(plugin.getDataFolder() + File.separator + "playerdata");
            if (!directory.exists() && !directory.isDirectory()) {
                directory.mkdirs();
            }
            file = new File(plugin.getDataFolder() + File.separator + "playerdata" + File.separator + playerUUID.toString() + ".yml");
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace(); //Making directories throws IOException too. Players will encounter this error only if they don't have enough space on disk
        }
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.set("quest-progress", null);
        for (QuestProgress questProgress : questProgress.values()) {
            if (!questProgress.isWorthSaving()) {
                continue;
            }
            data.set("quest-progress." + questProgress.getQuestId() + ".started", questProgress.isStarted());
            data.set("quest-progress." + questProgress.getQuestId() + ".completed", questProgress.isCompleted());
            data.set("quest-progress." + questProgress.getQuestId() + ".completed-before", questProgress.isCompletedBefore());
            data.set("quest-progress." + questProgress.getQuestId() + ".completion-date", questProgress.getCompletionDate());
            for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
                data.set("quest-progress." + questProgress.getQuestId() + ".task-progress." + taskProgress.getTaskId() + ".completed", taskProgress
                        .isCompleted());
                data.set("quest-progress." + questProgress.getQuestId() + ".task-progress." + taskProgress.getTaskId() + ".progress", taskProgress
                        .getProgress());
            }
        }

        try {
            data.save(file);
            if (disable)
                synchronized (questProgress) { //sync and async doesn't go well together
                    for (QuestProgress questProgress : questProgress.values()) {
                        questProgress.resetModified();
                    }
                }
            else
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    for (QuestProgress questProgress : questProgress.values()) {
                        questProgress.resetModified();
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        questProgress.clear();
    }
}

