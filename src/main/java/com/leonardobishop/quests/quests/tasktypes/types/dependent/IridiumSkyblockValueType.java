package com.leonardobishop.quests.quests.tasktypes.types.dependent;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.QuestsConfigLoader;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import com.leonardobishop.quests.quests.tasktypes.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class IridiumSkyblockValueType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();
    private BukkitTask poll;

    public IridiumSkyblockValueType() {
        super("iridiumskyblock_value", "LMBishop", "Reach a certain island value for Iridium Skyblock.");
        this.creatorConfigValues.add(new ConfigValue("value", true, "Minimum island value needed."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".value", config.get("value"), problems, "value", super.getType()))
            TaskUtils.configValidateInt(root + ".value", config.get("value"), problems, false, false, "value");
        return problems;
    }

    @Override
    public void onReady() {
        this.poll = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Island island = null;
                    if ((island = User.getUser(player).getIsland()) == null) {
                        return;
                    }

                    QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(player.getUniqueId(), true);
                    if (qPlayer == null) {
                        return;
                    }

                    QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

                    for (Quest quest : IridiumSkyblockValueType.super.getRegisteredQuests()) {
                        if (questProgressFile.hasStartedQuest(quest)) {
                            QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

                            for (Task task : quest.getTasksOfType(IridiumSkyblockValueType.super.getType())) {
                                TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                                if (taskProgress.isCompleted()
                                        || (taskProgress.getProgress() != null && (int) taskProgress.getProgress() == island.getValue())) {
                                    continue;
                                }

                                int islandValueNeeded = (int) task.getConfigValue("value");

                                taskProgress.setProgress(island.getValue());

                                if (((int) taskProgress.getProgress()) >= islandValueNeeded) {
                                    taskProgress.setCompleted(true);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Quests.get(), 50L, 50L);
    }

    @Override
    public void onDisable() {
        if (this.poll != null) {
            this.poll.cancel();
        }
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

}
