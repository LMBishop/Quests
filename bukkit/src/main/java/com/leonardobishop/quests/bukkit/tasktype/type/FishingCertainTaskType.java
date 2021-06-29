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
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FishingCertainTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;

    public FishingCertainTaskType(BukkitQuestsPlugin plugin) {
        super("fishingcertain", TaskUtils.TASK_ATTRIBUTION_STRING, "Catch a set amount of a specific item from the sea.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".item", config.get("item"), problems, "item", super.getType())) {
            if (Material.getMaterial(String.valueOf(config.get("item"))) == null) {
                problems.add(new ConfigProblem(ConfigProblem.ConfigProblemType.WARNING,
                        ConfigProblemDescriptions.UNKNOWN_MATERIAL.getDescription(String.valueOf(config.get("item"))), root + ".item.item"));
            }
        }
        if (TaskUtils.configValidateExists(root + ".amount", config.get("amount"), problems, "amount", super.getType()))
            TaskUtils.configValidateInt(root + ".amount", config.get("amount"), problems, false, true, "amount");
        TaskUtils.configValidateInt(root + ".data", config.get("data"), problems, true, false, "data");
        return problems;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFishCaught(PlayerFishEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) return;

        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        
        Player player = event.getPlayer();
        if (!(event.getCaught() instanceof Item)) {
            return;
        }
        Item caught = (Item) event.getCaught();
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

                    int catchesNeeded = (int) task.getConfigValue("amount");
                    String configItem = (String) task.getConfigValue("item");
                    Object configData = task.getConfigValue("data");

                    ItemStack is;
                    Material material = Material.getMaterial(String.valueOf(configItem));

                    if (material == null) {
                        continue;
                    }
                    if (configData != null) {
                        is = new ItemStack(material, 1, ((Integer) configData).shortValue());
                    } else {
                        is = new ItemStack(material, 1);
                    }

                    if (!caught.getItemStack().isSimilar(is)) {
                        return;
                    }

                    int progressCatches;
                    if (taskProgress.getProgress() == null) {
                        progressCatches = 0;
                    } else {
                        progressCatches = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressCatches + 1);

                    if (((int) taskProgress.getProgress()) >= catchesNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
