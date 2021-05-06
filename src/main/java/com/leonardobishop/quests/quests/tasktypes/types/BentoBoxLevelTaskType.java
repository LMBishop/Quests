package com.leonardobishop.quests.quests.tasktypes.types;

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
import org.bukkit.event.EventHandler;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.BentoBoxEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public final class BentoBoxLevelTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();
    private Field levelField = null;

    public BentoBoxLevelTaskType() {
        super("bentobox_level", "Rodney_Mc_Kay", "Reach a certain island level in the level addon for BentoBox.");
        this.creatorConfigValues.add(new ConfigValue("level", true, "Minimum island level needed."));
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
        if ("IslandLevelCalculatedEvent".equalsIgnoreCase(event.getEventName())) {
            QPlayer qPlayer = QuestsAPI.getPlayerManager().getPlayer((UUID) event.getKeyValues().get("targetPlayer"));
            if (qPlayer == null) {
                return;
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

                        Object results = event.getKeyValues().get("results");

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
