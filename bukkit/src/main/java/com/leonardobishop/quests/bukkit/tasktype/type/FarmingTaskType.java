package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FarmingTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public FarmingTaskType(BukkitQuestsPlugin plugin) {
        super("farming", TaskUtils.TASK_ATTRIBUTION_STRING, "Break or harvest a set amount of any crop.");
        this.plugin = plugin;

        try {
            Class.forName("org.bukkit.event.player.PlayerHarvestBlockEvent");
            plugin.getServer().getPluginManager().registerEvents(new FarmingTaskType.HarvestBlockListener(), plugin);
        } catch (ClassNotFoundException ignored) { } // server version cannot support event
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        handle(event.getPlayer(), event.getBlock(), "break");
    }

    private final class HarvestBlockListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onHarvestBlock(org.bukkit.event.player.PlayerHarvestBlockEvent event) {
            handle(event.getPlayer(), event.getHarvestedBlock(), "harvest");
        }
    }

    private void handle(Player player, Block block, String mode) {
        if (!(block.getState().getBlockData() instanceof Ageable)) {
            return;
        }

        Ageable crop = (Ageable) block.getState().getBlockData();
        if (crop.getAge() != crop.getMaximumAge()) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(player, task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    if (task.getConfigValue("mode") != null
                            && !mode.equalsIgnoreCase(task.getConfigValue("mode").toString())) {
                        continue;
                    }

                    int blocksNeeded = (int) task.getConfigValue("amount");

                    int progressBlocks;
                    if (taskProgress.getProgress() == null) {
                        progressBlocks = 0;
                    } else {
                        progressBlocks = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressBlocks + 1);

                    if (((int) taskProgress.getProgress()) >= blocksNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
