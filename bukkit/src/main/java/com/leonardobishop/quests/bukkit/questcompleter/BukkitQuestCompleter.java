package com.leonardobishop.quests.bukkit.questcompleter;


import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.QuestCompleter;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

//TODO move complete effects here ?
public class BukkitQuestCompleter implements QuestCompleter, Runnable {

    private final LinkedList<QuestProgress> completionQueue = new LinkedList<>();
    private final LinkedList<QuestProgressFile> fullCheckQueue = new LinkedList<>();
    private final LinkedList<UUID> expiredCheckQueue = new LinkedList<>();
    private final BukkitQuestsPlugin plugin;

    public BukkitQuestCompleter(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.processExpiredCheckQueue();
        this.processCompletionQueue();
        this.processFullCheckQueue();
    }

    private void checkExpiredQuests(QPlayer qPlayer) {
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
        for (QuestProgress questProgress : questProgressFile.getAllQuestProgress()) {
            if (!questProgress.isStarted()) {
                continue;
            }

            Quest quest = plugin.getQuestManager().getQuestById(questProgress.getQuestId());
            if (quest == null) {
                continue;
            }

            if (questProgressFile.getTimeRemainingFor(quest) == 0) {
                qPlayer.expireQuest(quest);
            }
        }
    }

    private void processExpiredCheckQueue() {
        UUID who = expiredCheckQueue.poll();
        if (who == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                expiredCheckQueue.add(player.getUniqueId());
            }
            return;
        }

        Player player = Bukkit.getPlayer(who);
        if (player == null || !player.isOnline()) return;

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) return;

        checkExpiredQuests(qPlayer);
    }

    private void processCompletionQueue() {
        QuestProgress questProgress = completionQueue.poll();
        if (questProgress == null) return;

        Player player = Bukkit.getPlayer(questProgress.getPlayerUUID());
        if (player != null && player.isOnline()) {
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) return;

            plugin.getQuestsLogger().debug("Processing player (singular: " + questProgress.getQuestId() + ") " + qPlayer.getPlayerUUID());

            checkExpiredQuests(qPlayer);

            Quest quest = plugin.getQuestManager().getQuestById(questProgress.getQuestId());

            if (!qPlayer.hasStartedQuest(quest)) return;

            if (checkComplete(quest, questProgress)) {
                qPlayer.completeQuest(quest);
            }
        }
    }

    private void processFullCheckQueue() {
        QuestProgressFile questProgressFile = fullCheckQueue.poll();
        if (questProgressFile == null) return;

        Player player = Bukkit.getPlayer(questProgressFile.getPlayerUUID());
        if (player != null && player.isOnline()) {
            QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (qPlayer == null) return;
            plugin.getQuestsLogger().debug("Processing player (full check) " + qPlayer.getPlayerUUID());

            checkExpiredQuests(qPlayer);

            for (QuestProgress questProgress : questProgressFile.getAllQuestProgress()) {
                Quest quest = plugin.getQuestManager().getQuestById(questProgress.getQuestId());
                if (quest == null) continue;
                if (!qPlayer.hasStartedQuest(quest)) continue;

                if (checkComplete(quest, questProgress)) {
                    qPlayer.completeQuest(quest);
                }
            }
        }
    }

    private boolean checkComplete(Quest quest, QuestProgress questProgress) {
        boolean complete = true;
        for (Task task : quest.getTasks()) {
            TaskProgress taskProgress;
            if ((taskProgress = questProgress.getTaskProgress(task.getId())) == null || !taskProgress.isCompleted()) {
                complete = false;
                break;
            }
        }

        return complete;
    }

    @Override
    public void queueSingular(@NotNull QuestProgress questProgress) {
        Objects.requireNonNull(questProgress, "questProgress cannot be null");

        completionQueue.add(questProgress);
    }

    @Override
    public void queueFullCheck(@NotNull QuestProgressFile questProgressFile) {
        Objects.requireNonNull(questProgressFile, "questProgressFile cannot be null");

        fullCheckQueue.add(questProgressFile);
    }

    public List<QuestProgress> getCompletionQueue() {
        return Collections.unmodifiableList(completionQueue);
    }

    public List<QuestProgressFile> getFullCheckQueue() {
        return Collections.unmodifiableList(fullCheckQueue);
    }

    public List<UUID> getExpiredCheckQueue() {
        return Collections.unmodifiableList(expiredCheckQueue);
    }
}
