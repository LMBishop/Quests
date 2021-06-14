package com.leonardobishop.quests.quest.controller;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.enums.QuestStartResult;
import com.leonardobishop.quests.api.events.PlayerCancelQuestEvent;
import com.leonardobishop.quests.api.events.PlayerFinishQuestEvent;
import com.leonardobishop.quests.api.events.PlayerStartQuestEvent;
import com.leonardobishop.quests.api.events.PreStartQuestEvent;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quest.Quest;
import com.leonardobishop.quests.quest.Task;
import com.leonardobishop.quests.util.Messages;
import com.leonardobishop.quests.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class NormalQuestController implements QuestController {

    private final Quests plugin;

    public NormalQuestController(Quests plugin) {
        this.plugin = plugin;
    }

    @Override
    public QuestStartResult startQuestForPlayer(QPlayer qPlayer, Quest quest) {
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        QuestStartResult code = canPlayerStartQuest(qPlayer, quest);
        if (player != null) {
            String questResultMessage = null;
            switch (code) {
                case QUEST_SUCCESS:
                    // This one is hacky
                    break;
                case QUEST_LIMIT_REACHED:
                    questResultMessage = Messages.QUEST_START_LIMIT.getMessage().replace("{limit}", String.valueOf(Options.QUESTS_START_LIMIT.getIntValue()));
                    break;
                case QUEST_ALREADY_COMPLETED:
                    questResultMessage = Messages.QUEST_START_DISABLED.getMessage();
                    break;
                case QUEST_COOLDOWN:
                    long cooldown = qPlayer.getQuestProgressFile().getCooldownFor(quest);
                    questResultMessage = Messages.QUEST_START_COOLDOWN.getMessage().replace("{time}", String.valueOf(plugin.convertToFormat(TimeUnit.SECONDS.convert
                            (cooldown, TimeUnit.MILLISECONDS))));
                    break;
                case QUEST_LOCKED:
                    questResultMessage = Messages.QUEST_START_LOCKED.getMessage();
                    break;
                case QUEST_ALREADY_STARTED:
                    questResultMessage = Messages.QUEST_START_STARTED.getMessage();
                    break;
                case QUEST_NO_PERMISSION:
                    questResultMessage = Messages.QUEST_START_PERMISSION.getMessage();
                    break;
                case NO_PERMISSION_FOR_CATEGORY:
                    questResultMessage = Messages.QUEST_CATEGORY_QUEST_PERMISSION.getMessage();
                    break;
            }
            // PreStartQuestEvent -- start
            PreStartQuestEvent preStartQuestEvent = new PreStartQuestEvent(player, qPlayer, questResultMessage, code);
            Bukkit.getPluginManager().callEvent(preStartQuestEvent);
            // PreStartQuestEvent -- end
            if (preStartQuestEvent.getQuestResultMessage() != null && code != QuestStartResult.QUEST_SUCCESS)
                player.sendMessage(preStartQuestEvent.getQuestResultMessage());
        }
        if (code == QuestStartResult.QUEST_SUCCESS) {
            QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
            questProgress.setStarted(true);
            for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
                taskProgress.setCompleted(false);
                taskProgress.setProgress(null);
            }
            if (Options.ALLOW_QUEST_TRACK.getBooleanValue() && Options.QUEST_AUTOTRACK.getBooleanValue()) {
                qPlayer.trackQuest(quest);
            }
            questProgress.setCompleted(false);
            if (player != null) {
                String questStartMessage = Messages.QUEST_START.getMessage().replace("{quest}", quest.getDisplayNameStripped());
                // PlayerStartQuestEvent -- start
                PlayerStartQuestEvent questStartEvent = new PlayerStartQuestEvent(player, qPlayer, questProgress, questStartMessage);
                Bukkit.getPluginManager().callEvent(questStartEvent);
                // PlayerStartQuestEvent -- end
                if (questStartEvent.getQuestStartMessage() != null)
                    player.sendMessage(questStartEvent.getQuestStartMessage()); //Don't send a message if the event message is null
                if (Options.TITLES_ENABLED.getBooleanValue()) {
                    plugin.getTitleHandle().sendTitle(player, Messages.TITLE_QUEST_START_TITLE.getMessage().replace("{quest}", quest
                            .getDisplayNameStripped()), Messages.TITLE_QUEST_START_SUBTITLE.getMessage().replace("{quest}", quest
                            .getDisplayNameStripped()));
                }
                for (String s : quest.getStartString()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
            }
            for (Task task : quest.getTasks()) {
                try {
                    plugin.getTaskTypeManager().getTaskType(task.getType()).onStart(quest, task, qPlayer.getPlayerUUID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return code;
    }

    @Override
    public QuestStartResult canPlayerStartQuest(QPlayer qPlayer, Quest quest) {
        Player p = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (qPlayer.getQuestProgressFile().getStartedQuests().size() >= Options.QUESTS_START_LIMIT.getIntValue() && !Options.QUEST_AUTOSTART.getBooleanValue()) {
            return QuestStartResult.QUEST_LIMIT_REACHED;
        }
        QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
        if (!quest.isRepeatable() && questProgress.isCompletedBefore()) {
            //if (playerUUID != null) {
            // ???
            //}
            return QuestStartResult.QUEST_ALREADY_COMPLETED;
        }
        long cooldown = qPlayer.getQuestProgressFile().getCooldownFor(quest);
        if (cooldown > 0) {
            return QuestStartResult.QUEST_COOLDOWN;
        }
        if (!qPlayer.getQuestProgressFile().hasMetRequirements(quest)) {
            return QuestStartResult.QUEST_LOCKED;
        }
        if (questProgress.isStarted()) {
            return QuestStartResult.QUEST_ALREADY_STARTED;
        }
        if (quest.isPermissionRequired()) {
            if (p != null) {
                if (!p.hasPermission("quests.quest." + quest.getId())) {
                    return QuestStartResult.QUEST_NO_PERMISSION;
                }
            } else {
                return QuestStartResult.QUEST_NO_PERMISSION;
            }
        }
        if (quest.getCategoryId() != null && plugin.getQuestManager().getCategoryById(quest.getCategoryId()) != null && plugin.getQuestManager()
                .getCategoryById(quest.getCategoryId()).isPermissionRequired()) {
            if (p != null) {
                if (!p.hasPermission("quests.category." + quest.getCategoryId())) {
                    return QuestStartResult.NO_PERMISSION_FOR_CATEGORY;
                }
            } else {
                return QuestStartResult.NO_PERMISSION_FOR_CATEGORY;
            }
        }
        return QuestStartResult.QUEST_SUCCESS;
    }

    @Override
    public boolean completeQuestForPlayer(QPlayer qPlayer, Quest quest) {
        QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
        questProgress.setStarted(false);
        for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
            taskProgress.setCompleted(false);
            taskProgress.setProgress(null);
        }
        questProgress.setCompleted(true);
        questProgress.setCompletedBefore(true);
        questProgress.setCompletionDate(System.currentTimeMillis());
        if (Options.ALLOW_QUEST_TRACK.getBooleanValue() && Options.QUEST_AUTOTRACK.getBooleanValue() && !(quest.isRepeatable() && !quest.isCooldownEnabled())) {
            qPlayer.trackQuest(null);
        }
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (player != null) {
            String questFinishMessage = Messages.QUEST_COMPLETE.getMessage().replace("{quest}", quest.getDisplayNameStripped());
            // PlayerFinishQuestEvent -- start
            PlayerFinishQuestEvent questFinishEvent = new PlayerFinishQuestEvent(player, qPlayer, questProgress, questFinishMessage);
            Bukkit.getPluginManager().callEvent(questFinishEvent);
            // PlayerFinishQuestEvent -- end
            Bukkit.getServer().getScheduler().runTask(plugin, () -> {
                for (String s : quest.getRewards()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replace("{player}", player.getName())); //TODO PlaceholderAPI support
                }
            });
            if (questFinishEvent.getQuestFinishMessage() != null)
                player.sendMessage(questFinishEvent.getQuestFinishMessage());
            if (Options.TITLES_ENABLED.getBooleanValue()) {
                plugin.getTitleHandle().sendTitle(player, Messages.TITLE_QUEST_COMPLETE_TITLE.getMessage().replace("{quest}", quest
                        .getDisplayNameStripped()), Messages.TITLE_QUEST_COMPLETE_SUBTITLE.getMessage().replace("{quest}", quest
                        .getDisplayNameStripped()));
            }
            for (String s : quest.getRewardString()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        if ((Options.ALLOW_QUEST_TRACK.getBooleanValue() && Options.QUEST_AUTOTRACK.getBooleanValue() && !(quest.isRepeatable() && !quest.isCooldownEnabled()))
                || (!Options.ALLOW_QUEST_TRACK.getBooleanValue() && Options.QUEST_AUTOTRACK.getBooleanValue())) {
            Quest nextQuest;
            if (qPlayer.getQuestProgressFile().getStartedQuests().size() > 0) {
                nextQuest = qPlayer.getQuestProgressFile().getStartedQuests().get(0);
                qPlayer.trackQuest(nextQuest);
            }
        }
        return true;
    }

    @Override
    public boolean hasPlayerStartedQuest(QPlayer qPlayer, Quest quest) {
        if (Options.QUEST_AUTOSTART.getBooleanValue()) {
            QuestStartResult response = canPlayerStartQuest(qPlayer, quest);
            return response == QuestStartResult.QUEST_SUCCESS || response == QuestStartResult.QUEST_ALREADY_STARTED;
        } else {
            return qPlayer.getQuestProgressFile().hasQuestProgress(quest) && qPlayer.getQuestProgressFile().getQuestProgress(quest).isStarted();
        }
    }

    @Override
    public boolean cancelQuestForPlayer(QPlayer qPlayer, Quest quest) {
        QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (!questProgress.isStarted()) {
            if (player != null) {
                player.sendMessage(Messages.QUEST_CANCEL_NOTSTARTED.getMessage());
            }
            return false;
        }
        questProgress.setStarted(false);
        for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
            taskProgress.setProgress(null);
        }
        if (player != null) {
            String questCancelMessage = Messages.QUEST_CANCEL.getMessage().replace("{quest}", quest.getDisplayNameStripped());
            // PlayerCancelQuestEvent -- start
            PlayerCancelQuestEvent questCancelEvent = new PlayerCancelQuestEvent(player, qPlayer, questProgress, questCancelMessage);
            Bukkit.getPluginManager().callEvent(questCancelEvent);
            // PlayerCancelQuestEvent -- end
            if (questCancelEvent.getQuestCancelMessage() != null)
                player.sendMessage(questCancelEvent.getQuestCancelMessage());
        }
        return true;
    }

}
