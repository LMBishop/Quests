package com.leonardobishop.quests.bukkit.questcontroller;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.api.event.PlayerCancelQuestEvent;
import com.leonardobishop.quests.bukkit.api.event.PlayerFinishQuestEvent;
import com.leonardobishop.quests.bukkit.api.event.PlayerStartQuestEvent;
import com.leonardobishop.quests.bukkit.api.event.PlayerStartTrackQuestEvent;
import com.leonardobishop.quests.bukkit.api.event.PlayerStopTrackQuestEvent;
import com.leonardobishop.quests.bukkit.api.event.PreStartQuestEvent;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStack;
import com.leonardobishop.quests.bukkit.util.Format;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.bukkit.util.SoundUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.questcontroller.QuestController;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NormalQuestController implements QuestController {

    private final BukkitQuestsPlugin plugin;
    private final BukkitQuestsConfig config;

    private final List<Quest> autoStartQuestCache;

    public NormalQuestController(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        this.config = (BukkitQuestsConfig) plugin.getQuestsConfig();

        List<Quest> autoStartQuestCache = new ArrayList<>();
        for (Quest quest : plugin.getQuestManager().getQuests().values()) {
            if (quest.isAutoStartEnabled()) autoStartQuestCache.add(quest);
        }
        this.autoStartQuestCache = autoStartQuestCache;
    }

    @Override
    public String getName() {
        return "normal";
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
                    questResultMessage = Messages.QUEST_START_LIMIT.getMessage().replace("{limit}", String.valueOf(config.getInt("options.quest-started-limit")));
                    break;
                case QUEST_ALREADY_COMPLETED:
                    questResultMessage = Messages.QUEST_START_DISABLED.getMessage();
                    break;
                case QUEST_COOLDOWN:
                    long cooldown = qPlayer.getQuestProgressFile().getCooldownFor(quest);
                    questResultMessage = Messages.QUEST_START_COOLDOWN.getMessage().replace("{time}", Format.formatTime(TimeUnit.SECONDS.convert
                            (cooldown, TimeUnit.MILLISECONDS)));
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
            if (config.getBoolean("options.allow-quest-track") && config.getBoolean("options.quest-autotrack")) {
                qPlayer.trackQuest(quest);
            }
            questProgress.setCompleted(false);
            if (player != null) {
                QItemStack qItemStack = plugin.getQItemStackRegistry().getQuestItemStack(quest);
                String displayName = Chat.strip(qItemStack.getName());
                String questStartMessage = Messages.QUEST_START.getMessage().replace("{quest}", displayName);
                // PlayerStartQuestEvent -- start
                PlayerStartQuestEvent questStartEvent = new PlayerStartQuestEvent(player, qPlayer, questProgress, questStartMessage);
                Bukkit.getPluginManager().callEvent(questStartEvent);
                // PlayerStartQuestEvent -- end
                if (questStartEvent.getQuestStartMessage() != null)
                    player.sendMessage(questStartEvent.getQuestStartMessage()); //Don't send a message if the event message is null
                if (config.getBoolean("options.titles-enabled")) {
                    plugin.getTitleHandle().sendTitle(player, Messages.TITLE_QUEST_START_TITLE.getMessage().replace("{quest}", displayName),
                            Messages.TITLE_QUEST_START_SUBTITLE.getMessage().replace("{quest}", displayName));
                }
                for (String s : quest.getStartString()) {
                    player.sendMessage(Chat.color(s));
                }
                SoundUtils.playSoundForPlayer(player, plugin.getQuestsConfig().getString("options.sounds.quest-start"));
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
        if (!config.getBoolean("options.quest-autostart") && getStartedQuestsForPlayer(qPlayer).size() >= config.getInt("options.quest-started-limit")) {
            return QuestStartResult.QUEST_LIMIT_REACHED;
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
        if (config.getBoolean("options.allow-quest-track")) {
            trackNextQuest(qPlayer, quest);
        }
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (player != null) {
            QItemStack qItemStack = plugin.getQItemStackRegistry().getQuestItemStack(quest);
            String displayName = Chat.strip(qItemStack.getName());
            String questFinishMessage = Messages.QUEST_COMPLETE.getMessage().replace("{quest}", displayName);
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
            if (config.getBoolean("options.titles-enabled")) {
                plugin.getTitleHandle().sendTitle(player, Messages.TITLE_QUEST_COMPLETE_TITLE.getMessage().replace("{quest}", displayName),
                        Messages.TITLE_QUEST_COMPLETE_SUBTITLE.getMessage().replace("{quest}", displayName));
            }
            for (String s : quest.getRewardString()) {
                player.sendMessage(Chat.color(s));
            }
            SoundUtils.playSoundForPlayer(player, plugin.getQuestsConfig().getString("options.sounds.quest-complete"));
        }
        return true;
    }

    @Override
    public boolean hasPlayerStartedQuest(QPlayer qPlayer, Quest quest) {
        if (config.getBoolean("options.quest-autostart")) {
            QuestStartResult response = canPlayerStartQuest(qPlayer, quest);
            return response == QuestStartResult.QUEST_SUCCESS || response == QuestStartResult.QUEST_ALREADY_STARTED;
        } else {
            if (quest.isAutoStartEnabled()) {
                QuestStartResult response = canPlayerStartQuest(qPlayer, quest);
                return response == QuestStartResult.QUEST_SUCCESS || response == QuestStartResult.QUEST_ALREADY_STARTED;
            } else return qPlayer.getQuestProgressFile().hasQuestProgress(quest) && qPlayer.getQuestProgressFile().getQuestProgress(quest).isStarted();
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
            QItemStack qItemStack = plugin.getQItemStackRegistry().getQuestItemStack(quest);
            String displayName = Chat.strip(qItemStack.getName());
            String questCancelMessage = Messages.QUEST_CANCEL.getMessage().replace("{quest}", displayName);
            // PlayerCancelQuestEvent -- start
            PlayerCancelQuestEvent questCancelEvent = new PlayerCancelQuestEvent(player, qPlayer, questProgress, questCancelMessage);
            Bukkit.getPluginManager().callEvent(questCancelEvent);
            // PlayerCancelQuestEvent -- end
            if (questCancelEvent.getQuestCancelMessage() != null) {
                player.sendMessage(questCancelEvent.getQuestCancelMessage());
            }
            SoundUtils.playSoundForPlayer(player, plugin.getQuestsConfig().getString("options.sounds.quest-cancel"));
        }
        if (config.getBoolean("options.allow-quest-track")
                && config.getBoolean("options.quest-autotrack")
                && quest.getId().equals(qPlayer.getPlayerPreferences().getTrackedQuestId())) {
            trackNextQuest(qPlayer, null);
        }
        return true;
    }

    @Override
    public void trackQuestForPlayer(QPlayer qPlayer, Quest quest) {
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());

        if (quest == null) {
            String currentTrackedQuestId = qPlayer.getPlayerPreferences().getTrackedQuestId();
            qPlayer.getPlayerPreferences().setTrackedQuestId(null);
            if (player != null) {
                Bukkit.getPluginManager().callEvent(new PlayerStopTrackQuestEvent(player, qPlayer));
                Quest currentTrackedQuest;
                if (currentTrackedQuestId != null && (currentTrackedQuest = plugin.getQuestManager().getQuestById(currentTrackedQuestId)) != null) {
                    QItemStack qItemStack = plugin.getQItemStackRegistry().getQuestItemStack(currentTrackedQuest);
                    String displayName = Chat.strip(qItemStack.getName());
                    player.sendMessage(Messages.QUEST_TRACK_STOP.getMessage().replace("{quest}", displayName));
                }
            }
        } else if (qPlayer.hasStartedQuest(quest)) {
            QItemStack qItemStack = plugin.getQItemStackRegistry().getQuestItemStack(quest);
            String displayName = Chat.strip(qItemStack.getName());
            qPlayer.getPlayerPreferences().setTrackedQuestId(quest.getId());
            if (player != null) {
                Bukkit.getPluginManager().callEvent(new PlayerStartTrackQuestEvent(player, qPlayer));
                player.sendMessage(Messages.QUEST_TRACK.getMessage().replace("{quest}", displayName));
            }
        }
    }

    private Set<Quest> getStartedQuestsForPlayer(QPlayer qPlayer) {
        Set<Quest> startedQuests = new HashSet<>();
        if (config.getBoolean("options.quest-autostart")) {
            for (Quest quest : plugin.getQuestManager().getQuests().values()) {
                QuestStartResult response = canPlayerStartQuest(qPlayer, quest);
                if (response == QuestStartResult.QUEST_SUCCESS || response == QuestStartResult.QUEST_ALREADY_STARTED) {
                    startedQuests.add(quest);
                }
            }
        } else {
            startedQuests.addAll(qPlayer.getQuestProgressFile().getStartedQuests());
            for (Quest quest : autoStartQuestCache) {
                QuestStartResult response = canPlayerStartQuest(qPlayer, quest);
                if (response == QuestStartResult.QUEST_SUCCESS || response == QuestStartResult.QUEST_ALREADY_STARTED) {
                    startedQuests.add(quest);
                }
            }
        }
        return startedQuests;
    }

    private void trackNextQuest(QPlayer qPlayer, Quest previousQuest) {
        if (config.getBoolean("options.quest-autotrack")
                && (previousQuest == null || !(previousQuest.isRepeatable() && !previousQuest.isCooldownEnabled()))) {
            Quest nextQuest;
            if (qPlayer.getQuestProgressFile().getStartedQuests().size() > 0) {
                nextQuest = qPlayer.getQuestProgressFile().getStartedQuests().get(0);
                qPlayer.trackQuest(nextQuest);
            } else {
                qPlayer.trackQuest(null);
            }
        } else if (!config.getBoolean("options.quest-autotrack")) {
            qPlayer.trackQuest(null);
        }
    }

}
