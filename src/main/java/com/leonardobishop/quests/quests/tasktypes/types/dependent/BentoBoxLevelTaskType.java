package com.leonardobishop.quests.quests.tasktypes.types.dependent;

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
import com.leonardobishop.quests.quests.tasktypes.TaskTypeManager;
import com.leonardobishop.quests.quests.tasktypes.TaskUtils;
import org.bukkit.event.EventHandler;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.BentoBoxEvent;
import world.bentobox.bentobox.database.objects.Island;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public final class BentoBoxLevelTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();
    private Field levelField = null;

    public BentoBoxLevelTaskType() {
        super("bentobox_level", "Rodney_Mc_Kay", "Reach a certain island level in the level addon for BentoBox.");
        this.creatorConfigValues.add(new ConfigValue("level", true, "Minimum island level needed."));
    }

    @Override
    public List<QuestsConfigLoader.ConfigProblem> detectProblemsInConfig(String root, HashMap<String, Object> config) {
        ArrayList<QuestsConfigLoader.ConfigProblem> problems = new ArrayList<>();
        if (TaskUtils.configValidateExists(root + ".level", config.get("level"), problems, "level", super.getType()))
            TaskUtils.configValidateInt(root + ".level", config.get("level"), problems, false, false, "level");
        return problems;
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    public static void register(TaskTypeManager manager) {
        if (BentoBox.getInstance().getAddonsManager().getAddonByName("Level").isPresent()) {
            manager.registerTaskType(new BentoBoxLevelTaskType());
        }
    }

    @EventHandler
    public void onBentoBoxIslandLevelCalculated(BentoBoxEvent event) {
        Map<String, Object> keyValues = event.getKeyValues();

        if ("IslandLevelCalculatedEvent".equalsIgnoreCase(event.getEventName())) {
            Island island = (Island) keyValues.get("island");

            for (UUID member : island.getMemberSet()) {
                QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer(member, true);
                if (qPlayer == null) {
                    continue;
                }

                QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();

                for (Quest quest : super.getRegisteredQuests()) {
                    if (questProgressFile.hasStartedQuest(quest)) {
                        QuestProgress questProgress = questProgressFile.getQuestProgress(quest);

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
