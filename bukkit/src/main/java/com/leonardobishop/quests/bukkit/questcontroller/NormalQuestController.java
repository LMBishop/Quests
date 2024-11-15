package com.leonardobishop.quests.bukkit.questcontroller;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.api.event.PlayerCancelQuestEvent;
import com.leonardobishop.quests.bukkit.api.event.PlayerExpireQuestEvent;
import com.leonardobishop.quests.bukkit.api.event.PlayerFinishQuestEvent;
import com.leonardobishop.quests.bukkit.api.event.PlayerStartQuestEvent;
import com.leonardobishop.quests.bukkit.api.event.PlayerStartTrackQuestEvent;
import com.leonardobishop.quests.bukkit.api.event.PlayerStopTrackQuestEvent;
import com.leonardobishop.quests.bukkit.api.event.PreStartQuestEvent;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.hook.vault.rewards.VaultReward;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStack;
import com.leonardobishop.quests.bukkit.util.FormatUtils;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class NormalQuestController implements QuestController {

    private final BukkitQuestsPlugin plugin;
    private final BukkitQuestsConfig config;

    private final List<Quest> autoStartQuestCache;
    private final Map<Quest, VaultReward> vaultRewardCache;

    public NormalQuestController(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        this.config = (BukkitQuestsConfig) plugin.getQuestsConfig();

        List<Quest> autoStartQuestCache = new ArrayList<>();
        for (Quest quest : plugin.getQuestManager().getQuests().values()) {
            if (quest.isAutoStartEnabled()) autoStartQuestCache.add(quest);
        }
        this.autoStartQuestCache = autoStartQuestCache;

        this.vaultRewardCache = new WeakHashMap<>();
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
                    questResultMessage = Messages.QUEST_START_LIMIT.getMessage().replace("{limit}", String.valueOf(config.getQuestLimit(player)));
                    break;
                case QUEST_ALREADY_COMPLETED:
                    questResultMessage = Messages.QUEST_START_DISABLED.getMessage();
                    break;
                case QUEST_COOLDOWN:
                    long cooldown = qPlayer.getQuestProgressFile().getCooldownFor(quest);
                    questResultMessage = Messages.QUEST_START_COOLDOWN.getMessage().replace("{time}", FormatUtils.time(TimeUnit.SECONDS.convert
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

            code = preStartQuestEvent.getQuestStartResult();

            // PreStartQuestEvent -- end
            if (code != QuestStartResult.QUEST_SUCCESS) {
                Messages.send(preStartQuestEvent.getQuestResultMessage(), player);
            }
        }
        if (code == QuestStartResult.QUEST_SUCCESS) {
            QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
            questProgress.setStarted(true);
            questProgress.setStartedDate(System.currentTimeMillis());
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
                String displayName = qItemStack.getName();
                String displayNameStripped = Chat.legacyStrip(displayName);
                String questStartMessage = Messages.QUEST_START.getMessage().replace("{quest}", displayNameStripped).replace("{questcolored}", displayName);
                // PlayerStartQuestEvent -- start
                PlayerStartQuestEvent questStartEvent = new PlayerStartQuestEvent(player, qPlayer, questProgress, questStartMessage);
                Bukkit.getPluginManager().callEvent(questStartEvent);
                // PlayerStartQuestEvent -- end
                Messages.send(questStartEvent.getQuestStartMessage(), player);
                if (config.getBoolean("options.titles-enabled")) {
                    plugin.getTitleHandle().sendTitle(player, Messages.TITLE_QUEST_START_TITLE.getMessageLegacyColor().replace("{quest}", displayNameStripped).replace("{questcolored}", displayName),
                            Messages.TITLE_QUEST_START_SUBTITLE.getMessageLegacyColor().replace("{quest}", displayNameStripped).replace("{questcolored}", displayName));
                }
                for (String s : quest.getStartCommands()) {
                    s = s.replace("{player}", player.getName());
                    if (plugin.getConfig().getBoolean("options.quests-use-placeholderapi")) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), plugin.getPlaceholderAPIProcessor().apply(player, s));
                    } else {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
                    }
                }
                for (String s : quest.getStartString()) {
                    if (plugin.getConfig().getBoolean("options.quests-use-placeholderapi")) {
                        s = plugin.getPlaceholderAPIProcessor().apply(player, s);
                    }
                    Chat.send(player, s, true);
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

        boolean autostart = this.config.getBoolean("options.quest-autostart");
        if (questProgress.isStarted() || quest.isAutoStartEnabled() || autostart) {
            return QuestStartResult.QUEST_ALREADY_STARTED;
        }

        if (quest.doesCountTowardsLimit()) {
            Set<Quest> startedQuests = getStartedQuestsForPlayer(qPlayer);
            int questLimitCount = 0;
            for (Quest q : startedQuests) {
                if (q.doesCountTowardsLimit()) {
                    questLimitCount++;
                }
            }
            if (questLimitCount >= config.getQuestLimit(p)) {
                return QuestStartResult.QUEST_LIMIT_REACHED;
            }
        }

        return QuestStartResult.QUEST_SUCCESS;
    }

    @Override
    public boolean completeQuestForPlayer(QPlayer qPlayer, Quest quest) {
        QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
        questProgress.setStarted(false);
        questProgress.setStartedDate(System.currentTimeMillis());
        for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
            taskProgress.setCompleted(false);
            taskProgress.setProgress(null);
        }
        questProgress.setCompleted(true);
        questProgress.setCompletedBefore(true);
        questProgress.setCompletionDate(System.currentTimeMillis());
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (player != null) {
            QItemStack qItemStack = plugin.getQItemStackRegistry().getQuestItemStack(quest);
            String displayName = qItemStack.getName();
            String displayNameStripped = Chat.legacyStrip(displayName);
            String questFinishMessage = Messages.QUEST_COMPLETE.getMessage().replace("{quest}", displayNameStripped).replace("{questcolored}", displayName);
            // PlayerFinishQuestEvent -- start
            PlayerFinishQuestEvent questFinishEvent = new PlayerFinishQuestEvent(player, qPlayer, questProgress, questFinishMessage);
            Bukkit.getPluginManager().callEvent(questFinishEvent);
            // PlayerFinishQuestEvent -- end
            plugin.getScheduler().doSync(() -> {
                final VaultReward vaultReward = this.vaultRewardCache.computeIfAbsent(quest,
                        k -> VaultReward.parse(this.plugin, k.getVaultReward())
                );

                // Use cached reward to do not parse it every single time
                vaultReward.give(player);

                for (String s : quest.getRewards()) {
                    s = s.replace("{player}", player.getName());
                    if (plugin.getConfig().getBoolean("options.quests-use-placeholderapi")) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), plugin.getPlaceholderAPIProcessor().apply(player, s));
                    } else {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
                    }
                }
            });
            Messages.send(questFinishEvent.getQuestFinishMessage(), player);
            if (config.getBoolean("options.titles-enabled")) {
                plugin.getTitleHandle().sendTitle(player, Messages.TITLE_QUEST_COMPLETE_TITLE.getMessageLegacyColor().replace("{quest}", displayNameStripped).replace("{questcolored}", displayName),
                        Messages.TITLE_QUEST_COMPLETE_SUBTITLE.getMessageLegacyColor().replace("{quest}", displayNameStripped).replace("{questcolored}", displayName));
            }
            for (String s : quest.getRewardString()) {
                if (plugin.getConfig().getBoolean("options.quests-use-placeholderapi")) {
                    s = plugin.getPlaceholderAPIProcessor().apply(player, s);
                }
                player.sendMessage(Chat.legacyColor(s));
            }
            SoundUtils.playSoundForPlayer(player, plugin.getQuestsConfig().getString("options.sounds.quest-complete"));
        }
        if (this.config.getBoolean("options.allow-quest-track") && this.config.getBoolean("options.quest-autotrack")) {
            this.trackNextQuest(qPlayer, quest);
        }
        return true;
    }

    @Override
    public boolean hasPlayerStartedQuest(QPlayer qPlayer, Quest quest) {
        return quest.isAutoStartEnabled() || config.getBoolean("options.quest-autostart")
                ? canPlayerStartQuest(qPlayer, quest).hasPlayerStartedQuest()
                : qPlayer.getQuestProgressFile().hasQuestStarted(quest);
    }

    private void resetQuest(QuestProgress questProgress) {
        questProgress.setStarted(false);
        questProgress.setStartedDate(System.currentTimeMillis());
        for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
            taskProgress.setCompleted(false);
            taskProgress.setProgress(null);
        }
    }

    @Override
    public boolean cancelQuestForPlayer(QPlayer qPlayer, Quest quest) {
        QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (!questProgress.isStarted()) {
            if (player != null) {
                Messages.QUEST_CANCEL_NOTSTARTED.send(player);
            }
            return false;
        }
        if (!quest.isCancellable() || quest.isAutoStartEnabled() || config.getBoolean("options.quest-autostart")) {
            Messages.QUEST_CANCEL_NOTCANCELLABLE.send(player);
            return false;
        }
       resetQuest(questProgress);
        if (player != null) {
            QItemStack qItemStack = plugin.getQItemStackRegistry().getQuestItemStack(quest);
            String displayName = qItemStack.getName();
            String displayNameStripped = Chat.legacyStrip(displayName);
            String questCancelMessage = Messages.QUEST_CANCEL.getMessage().replace("{quest}", displayNameStripped).replace("{questcolored}", displayName);
            // PlayerCancelQuestEvent -- start
            PlayerCancelQuestEvent questCancelEvent = new PlayerCancelQuestEvent(player, qPlayer, questProgress, questCancelMessage);
            Bukkit.getPluginManager().callEvent(questCancelEvent);
            // PlayerCancelQuestEvent -- end
            Messages.send(questCancelEvent.getQuestCancelMessage(), player);
            for (String s : quest.getCancelCommands()) {
                s = s.replace("{player}", player.getName());
                if (plugin.getConfig().getBoolean("options.quests-use-placeholderapi")) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), plugin.getPlaceholderAPIProcessor().apply(player, s));
                } else {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
                }
            }
            for (String s : quest.getCancelString()) {
                if (plugin.getConfig().getBoolean("options.quests-use-placeholderapi")) {
                    s = plugin.getPlaceholderAPIProcessor().apply(player, s);
                }
                player.sendMessage(Chat.legacyColor(s));
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
    public boolean expireQuestForPlayer(QPlayer qPlayer, Quest quest) {
        QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
        if (!questProgress.isStarted()) {
            return false;
        }
        resetQuest(questProgress);
        if (player != null) {
            QItemStack qItemStack = plugin.getQItemStackRegistry().getQuestItemStack(quest);
            String displayName = qItemStack.getName();
            String displayNameStripped = Chat.legacyStrip(displayName);
            String questExpireMessage = Messages.QUEST_EXPIRE.getMessage().replace("{quest}", displayNameStripped).replace("{questcolored}", displayName);
            // PlayerCancelQuestEvent -- start
            PlayerExpireQuestEvent questCancelEvent = new PlayerExpireQuestEvent(player, qPlayer, questProgress, questExpireMessage);
            Bukkit.getPluginManager().callEvent(questCancelEvent);
            // PlayerCancelQuestEvent -- end
            Messages.send(questCancelEvent.getQuestExpireMessage(), player);
            for (String s : quest.getExpiryCommands()) {
                s = s.replace("{player}", player.getName());
                if (plugin.getConfig().getBoolean("options.quests-use-placeholderapi")) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), plugin.getPlaceholderAPIProcessor().apply(player, s));
                } else {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
                }
            }
            for (String s : quest.getExpiryString()) {
                if (plugin.getConfig().getBoolean("options.quests-use-placeholderapi")) {
                    s = plugin.getPlaceholderAPIProcessor().apply(player, s);
                }
                player.sendMessage(Chat.legacyColor(s));
            }
        }
        if (config.getBoolean("options.allow-quest-track")
                && config.getBoolean("options.quest-autotrack")
                && quest.getId().equals(qPlayer.getPlayerPreferences().getTrackedQuestId())) {
            trackNextQuest(qPlayer, null);
        }
        return true;
    }

    @Override
    public void trackQuestForPlayer(final @NotNull QPlayer qPlayer, final @Nullable Quest quest) {
        final String trackedQuestId = qPlayer.getPlayerPreferences().getTrackedQuestId();
        final String questId = quest != null ? quest.getId() : null;

        if (!Objects.equals(questId, trackedQuestId)) {
            qPlayer.getPlayerPreferences().setTrackedQuestId(questId); // always update it
        } else {
            return; // no need to send messages or call events again
        }

        final Player player = this.plugin.getServer().getPlayer(qPlayer.getPlayerUUID());
        if (player == null) {
            return; // we can't even send a message or call an event without the player
        }

        if (quest == null) {
            this.plugin.getServer().getPluginManager().callEvent(new PlayerStopTrackQuestEvent(player, qPlayer));

            // we can assume that trackedQuestId != null because of the Objects#equals check above
            final Quest trackedQuest = this.plugin.getQuestManager().getQuestById(trackedQuestId);

            // it can be still null if the quest was removed
            if (trackedQuest != null) {
                final QItemStack item = this.plugin.getQItemStackRegistry().getQuestItemStack(trackedQuest);
                final String displayName = item.getName();
                final String displayNameStripped = Chat.legacyStrip(displayName);
                Messages.QUEST_TRACK_STOP.send(player, "{questcolored}", displayName, "{quest}", displayNameStripped);
            }

            // return and handle quest != null case
            return;
        }

        // we don't want to notify player about start of a quest which hasn't been started
        if (!qPlayer.hasStartedQuest(quest)) {
            return;
        }

        this.plugin.getServer().getPluginManager().callEvent(new PlayerStartTrackQuestEvent(player, qPlayer));

        final QItemStack item = this.plugin.getQItemStackRegistry().getQuestItemStack(quest);
        final String displayName = item.getName();
        final String displayNameStripped = Chat.legacyStrip(displayName);
        Messages.QUEST_TRACK.send(player, "{questcolored}", displayName, "{quest}", displayNameStripped);
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

    private void trackNextQuest(final @NotNull QPlayer qPlayer, final @Nullable Quest previousQuest) {
        final boolean autotrack = this.config.getBoolean("options.quest-autotrack");

        if (!autotrack) {
            qPlayer.trackQuest(null);
            return;
        }

        if (previousQuest == null || !previousQuest.isRepeatable() || previousQuest.isCooldownEnabled()) {
            final List<Quest> startedQuests = qPlayer.getQuestProgressFile().getStartedQuests();

            final Quest nextQuest;
            if (!startedQuests.isEmpty()) {
                nextQuest = startedQuests.getFirst();
            } else {
                final List<Quest> effectiveStartedQuests = qPlayer.getEffectiveStartedQuests(1);

                if (!effectiveStartedQuests.isEmpty()) {
                    nextQuest = effectiveStartedQuests.getFirst();
                } else {
                    nextQuest = null;
                }
            }

            qPlayer.trackQuest(nextQuest);
        }
    }
}
