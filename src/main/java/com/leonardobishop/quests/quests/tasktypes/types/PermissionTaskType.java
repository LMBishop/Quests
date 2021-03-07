package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.api.QuestsAPI;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public final class PermissionTaskType extends TaskType {

    private Quests plugin;
    private BukkitTask poll;
    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public PermissionTaskType(Quests plugin) {
        super("permission", "LMBishop", "Test if a player has a permission");
        this.plugin = plugin;
        this.creatorConfigValues.add(new ConfigValue("permission", true, "The required permission."));
    }

    @Override
    public void onReady() {
        this.poll = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(player.getUniqueId());
                    if (qPlayer == null) {
                        continue;
                    }
                    for (Quest quest : PermissionTaskType.super.getRegisteredQuests()) {
                        if (qPlayer.hasStartedQuest(quest)) {
                            QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
                            for (Task task : quest.getTasksOfType(PermissionTaskType.super.getType())) {
                                TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());
                                if (taskProgress.isCompleted()) {
                                    continue;
                                }
                                String permission = (String) task.getConfigValue("permission");
                                if (permission != null) {
                                    if (player.hasPermission(permission)) {
                                        taskProgress.setCompleted(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 30L, 30L);
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
