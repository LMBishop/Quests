package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class PermissionTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private BukkitTask poll;

    public PermissionTaskType(BukkitQuestsPlugin plugin) {
        super("permission", TaskUtils.TASK_ATTRIBUTION_STRING, "Test if a player has a permission");
        this.plugin = plugin;
    }

    @Override
    public void onReady() {
        this.poll = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
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

}
