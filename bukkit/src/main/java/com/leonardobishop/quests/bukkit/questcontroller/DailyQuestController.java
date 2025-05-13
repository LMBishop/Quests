//package com.leonardobishop.quests.bukkit.questcontroller;
//
//import com.leonardobishop.quests.common.enums.QuestStartResult;
//import com.leonardobishop.quests.common.player.QPlayer;
//import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
//import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
//import com.leonardobishop.quests.common.plugin.Quests;
//import com.leonardobishop.quests.common.quest.Quest;
//import com.leonardobishop.quests.common.questcontroller.QuestController;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.entity.Player;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.scheduler.BukkitTask;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.stream.Collectors;
//
////TODO finish this
//public class DailyQuestController implements QuestController {
//
//    private int refreshTaskId = -1;
//    private long refreshTime;
//    private long startTime;
//    private Quests plugin;
//    private Random random;
//    private List<String> quests;
//
//    public DailyQuestController(Quests plugin) {
//        this.plugin = plugin;
//        refreshDailyQuests();
//        scheduleNewTask();
//    }
//
//    public void cancel() {
//        Bukkit.getScheduler().cancelTask(refreshTaskId);
//    }
//
//    public List<String> getQuests() {
//        return quests;
//    }
//
//    private void scheduleNewTask() {
//        long diff = refreshTime - System.currentTimeMillis();
//        BukkitTask refreshTask;
//        if (diff <= 10000) { //10 sec
//            refreshTask = new DailyQuestRefreshTask(true).runTaskTimer(plugin, 1L, 1L);
//            plugin.getQuestsLogger().debug("DailyQuestRefreshTask set repeating (diff=" + diff + ")");
//        } else {
//            long sleepTime = diff >> 6;
//            plugin.getQuestsLogger().debug("DailyQuestRefreshTask slept for " + sleepTime + " ticks (diff=" + diff + ")");
//            refreshTask = new DailyQuestRefreshTask(false).runTaskLater(plugin, sleepTime);
//        }
//        refreshTaskId = refreshTask.getTaskId();
//    }
//
//    private void refreshDailyQuests() {
////        refreshTime = ((System.currentTimeMillis() / (86400000)) + 1) * 86400000;
//        refreshTime = ((System.currentTimeMillis() / (300000)) + 1) * 300000;
//        startTime = (System.currentTimeMillis() / (300000)) * 300000;
//        random = new Random(refreshTime);
//        quests = new ArrayList<>();
//
//        List<String> questIds = new ArrayList<>(plugin.getQuestManager().getQuestMap().keySet());
//        for (int i = 0; i < 5; i++) {
//            int randInt = random.nextInt(questIds.size());
//            quests.add(questIds.get(randInt));
//            questIds = questIds.stream().filter(s -> !quests.contains(s)).collect(Collectors.toList());
//        }
//    }
//
//    @Override
//    public QuestStartResult startQuestForPlayer(QPlayer qPlayer, Quest quest) {
//        if (quests.contains(quest.getId())) {
//            return QuestStartResult.QUEST_ALREADY_STARTED;
//        } else {
//            return QuestStartResult.QUEST_LIMIT_REACHED;
//        }
//    }
//
//    @Override
//    public QuestStartResult canPlayerStartQuest(QPlayer qPlayer, Quest quest) {
//        if (!quests.contains(quest.getId())) return QuestStartResult.OTHER;
//        long completionDate = qPlayer.getQuestProgressFile().getQuestProgress(quest).getCompletionDate();
//        if (Options.QUEST_AUTOSTART.getBooleanValue()) {
//            if (completionDate > startTime && completionDate <= refreshTime) {
//                return QuestStartResult.QUEST_ALREADY_COMPLETED;
//            }
//        } else {
//            if (qPlayer.getQuestProgressFile().hasQuestProgress(quest)) {
//                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
//                if (questProgress.isStarted() && completionDate > startTime && completionDate <= refreshTime) {
//                    return QuestStartResult.QUEST_ALREADY_STARTED;
//                }
//            }
//        }
//        return QuestStartResult.QUEST_SUCCESS;
//    }
//
//    @Override
//    public boolean completeQuestForPlayer(QPlayer qPlayer, Quest quest) {
//        QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
//        questProgress.setStarted(false);
//        for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
//            taskProgress.setCompleted(false);
//            taskProgress.setProgress(null);
//        }
//        questProgress.setCompleted(true);
//        questProgress.setCompletedBefore(true);
//        questProgress.setCompletionDate(System.currentTimeMillis());
//
//        boolean trackedReset = quest.getId().equals(qPlayer.getPlayerPreferences().getTrackedQuestId());
//        if (trackedReset) {
//            qPlayer.trackQuest(null);
//        }
//
//        Player player = Bukkit.getPlayer(qPlayer.getPlayerUUID());
//        if (player != null) {
//            String questFinishMessage = Messages.QUEST_COMPLETE.getMessage().replace("{quest}", quest.getDisplayNameStripped());
//            // PlayerFinishQuestEvent -- start
//            PlayerFinishQuestEvent questFinishEvent = new PlayerFinishQuestEvent(player, qPlayer, questProgress, questFinishMessage);
//            Bukkit.getPluginManager().callEvent(questFinishEvent);
//            // PlayerFinishQuestEvent -- end
//            Bukkit.getServer().getScheduler().runTask(plugin, () -> {
//                for (String s : quest.getRewards()) {
//                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replace("{player}", player.getName())); //TODO PlaceholderAPI support
//                }
//            });
//            if (questFinishEvent.getQuestFinishMessage() != null)
//                player.sendMessage(questFinishEvent.getQuestFinishMessage());
//            if (Options.TITLES_ENABLED.getBooleanValue()) {
//                plugin.getTitleHandle().sendTitle(player, Messages.TITLE_QUEST_COMPLETE_TITLE.getMessage().replace("{quest}", quest
//                        .getDisplayNameStripped()), Messages.TITLE_QUEST_COMPLETE_SUBTITLE.getMessage().replace("{quest}", quest
//                        .getDisplayNameStripped()));
//            }
//            for (String s : quest.getRewardString()) {
//                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
//            }
//        }
//        if (Options.QUEST_AUTOTRACK.getBooleanValue() && trackedReset) {
//            for (String s : quests) {
//                Quest nextQuest = plugin.getQuestManager().getQuestById(s);
//                if (nextQuest != null && canPlayerStartQuest(qPlayer, nextQuest) == QuestStartResult.QUEST_SUCCESS) {
//                    qPlayer.trackQuest(nextQuest);
//                    break;
//                }
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public boolean hasPlayerStartedQuest(QPlayer qPlayer, Quest quest) {
//        return canPlayerStartQuest(qPlayer, quest) == QuestStartResult.QUEST_SUCCESS;
//    }
//
//    @Override
//    public boolean cancelQuestForPlayer(QPlayer qPlayer, Quest quest) {
//        return false;
//    }
//
//    public class DailyQuestRefreshTask extends BukkitRunnable {
//
//        private final boolean repeating;
//
//        public DailyQuestRefreshTask(boolean repeating) {
//            this.repeating = repeating;
//        }
//
//        @Override
//        public void run() {
//            if (System.currentTimeMillis() >= refreshTime) {
//                this.cancel();
//                refreshDailyQuests();
//            } else {
//                if (repeating) return;
//                this.cancel();
//            }
//            scheduleNewTask();
//        }
//    }
//}
