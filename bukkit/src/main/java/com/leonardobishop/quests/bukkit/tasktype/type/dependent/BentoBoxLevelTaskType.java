package com.leonardobishop.quests.bukkit.tasktype.type.dependent;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.BentoBoxEvent;
import world.bentobox.bentobox.database.objects.Island;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public final class BentoBoxLevelTaskType extends BukkitTaskType {

    private final BukkitQuestsPlugin plugin;
    private Field levelField = null;

    public BentoBoxLevelTaskType(BukkitQuestsPlugin plugin) {
        super("bentobox_level", TaskUtils.TASK_ATTRIBUTION_STRING, "Reach a certain island level in the level addon for BentoBox.");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigProblem> validateConfig(@NotNull String root, @NotNull HashMap<String, Object> config) {
        ArrayList<ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".level", config.get("level"), problems, "level", super.getType()))
            TaskUtils.configValidateInt(root + ".level", config.get("level"), problems, false, false, "level");
        return problems;
    }


    public static void register(BukkitQuestsPlugin plugin, TaskTypeManager manager) {
        if (BentoBox.getInstance().getAddonsManager().getAddonByName("Level").isPresent()) {
            manager.registerTaskType(new BentoBoxLevelTaskType(plugin));
        }
    }

    @EventHandler
    public void onBentoBoxIslandLevelCalculated(BentoBoxEvent event) {
        Map<String, Object> keyValues = event.getKeyValues();

        if ("IslandLevelCalculatedEvent".equalsIgnoreCase(event.getEventName())) {
            Island island = (Island) keyValues.get("island");

            for (UUID member : island.getMemberSet()) {
                QPlayer qPlayer = plugin.getPlayerManager().getPlayer(member);
                if (qPlayer == null) {
                    continue;
                }

                for (Quest quest : super.getRegisteredQuests()) {
                    if (qPlayer.hasStartedQuest(quest)) {
                        QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);

                        for (Task task : quest.getTasksOfType(super.getType())) {
                            TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());

                            if (taskProgress.isCompleted()) {
                                continue;
                            }

                            long islandLevelNeeded = (long) (int) task.getConfigValue("level");

                            Object results = keyValues.get("results");

                            try {
                                if (levelField == null) {
                                    levelField = results.getClass().getDeclaredField("level");
                                    levelField.setAccessible(true);
                                }

                                AtomicLong level = (AtomicLong) levelField.get(results);
                                taskProgress.setProgress(level.get());
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }

                            if (((long) taskProgress.getProgress()) >= islandLevelNeeded) {
                                taskProgress.setCompleted(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
