package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblemDescriptions;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FarmingCertainTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public FarmingCertainTaskType(BukkitQuestsPlugin plugin) {
        super("farmingcertain", TaskUtils.TASK_ATTRIBUTION_STRING, "Break a set amount of a certain crop.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        //TODO add world validation
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        if (config.get("block") == null && config.get("blocks") == null) {
            TaskUtils.configValidateExists(root + ".block", config.get("block"), problems, "block", super.getType());
        } else {
            Object configBlock;
            String source;
            if (config.containsKey("block")) {
                source = "block";
            } else {
                source = "blocks";
            }
            configBlock = config.get(source);
            List<String> checkBlocks = new ArrayList<>();
            if (configBlock instanceof List) {
                checkBlocks.addAll((List) configBlock);
            } else {
                checkBlocks.add(String.valueOf(configBlock));
            }

            for (String materialName : checkBlocks) {
                if (Material.getMaterial(String.valueOf(materialName)) == null) {
                    problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                            ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(materialName), root + "." + source));
                }
            }
        }
        return problems;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState().getBlockData() instanceof Ageable)) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (qPlayer == null) {
            return;
        }

        Ageable crop = (Ageable) event.getBlock().getState().getBlockData();
        if (crop.getAge() != crop.getMaximumAge()) {
            return;
        }

        for (Quest quest : super.getRegisteredQuests()) {
            if (qPlayer.hasStartedQuest(quest)) {
                QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                for (Task task : quest.getTasksOfType(super.getType())) {
                    if (!TaskUtils.validateWorld(event.getPlayer(), task)) continue;

                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                    if (taskProgress.isCompleted()) {
                        continue;
                    }

                    if (matchBlock(task, event.getBlock())) {
                        int brokenBlocksNeeded = (int) task.getConfigValue("amount");

                        int progressBlocksBroken;
                        if (taskProgress.getProgress() == null) {
                            progressBlocksBroken = 0;
                        } else {
                            progressBlocksBroken = (int) taskProgress.getProgress();
                        }

                        taskProgress.setProgress(progressBlocksBroken + 1);

                        if (((int) taskProgress.getProgress()) >= brokenBlocksNeeded) {
                            taskProgress.setCompleted(true);
                        }
                    }
                }
            }
        }
    }

    private boolean matchBlock(Task task, Block block) {
        Material material;

        Object configBlock = task.getConfigValues().containsKey("block") ? task.getConfigValue("block") : task.getConfigValue("blocks");
        Object configData = task.getConfigValue("data");

        List<String> checkBlocks = new ArrayList<>();
        if (configBlock instanceof List) {
            checkBlocks.addAll((List) configBlock);
        } else {
            checkBlocks.add(String.valueOf(configBlock));
        }

        for (String materialName : checkBlocks) {
            material = Material.getMaterial(String.valueOf(materialName));
            Material blockType = block.getType();

            if (blockType == material) {
                return true;
            }
        }
        return false;
    }

}
