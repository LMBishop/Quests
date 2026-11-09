package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.constraint.TaskConstraintSet;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.questcontroller.QuestController;
import com.leonardobishop.quests.common.tasktype.TaskType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskUtilsTest {
    @BeforeAll static void initializePluginReference() throws Exception {
        try (MockedStatic<JavaPlugin> plugins = mockStatic(JavaPlugin.class)) {
            plugins.when(() -> JavaPlugin.getPlugin(BukkitQuestsPlugin.class)).thenReturn(mock(BukkitQuestsPlugin.class));
            Class.forName(TaskUtils.class.getName());
        }
    }

    @Test void theExistingOverloadStillChecksEveryRegisteredQuest() {
        Player player = mock(Player.class);
        QPlayer qPlayer = mock(QPlayer.class);
        QuestController controller = mock(QuestController.class, CALLS_REAL_METHODS);
        when(qPlayer.getQuestController()).thenReturn(controller);
        TaskType type = mock(TaskType.class);
        Quest first = mock(Quest.class), second = mock(Quest.class);
        when(type.getType()).thenReturn("mobkilling");
        when(type.getRegisteredQuests()).thenReturn(List.of(first, second));
        assertTrue(TaskUtils.getApplicableTasks(player, qPlayer, type).isEmpty());
        verify(qPlayer).hasStartedQuest(first);
        verify(qPlayer).hasStartedQuest(second);
    }

    @Test void aTypeWithoutQuestsDoesNotConsultPlayerProgressOrController() {
        QPlayer qPlayer = mock(QPlayer.class);
        TaskType type = mock(TaskType.class);
        when(type.getRegisteredQuests()).thenReturn(List.of());
        assertTrue(TaskUtils.getApplicableTasks(mock(Player.class), qPlayer, type).isEmpty());
        verifyNoInteractions(qPlayer);
    }

    @Test void tasksWithoutSpawnRestrictionsDoNotQueryTheEntitySpawnReason() {
        Task task = new Task("kill", "mobkilling");
        Entity entity = mock(Entity.class);
        TaskUtils.PendingTask pending = new TaskUtils.PendingTask(null, task, null, null);
        assertTrue(TaskUtils.matchSpawnReason(null, pending, entity, UUID.randomUUID()));
        verifyNoInteractions(entity);
    }

    @Test void eligibleTasksKeepRegistrationOrderAndSkipCompletedProgress() {
        QPlayer qPlayer = mock(QPlayer.class);
        QuestController controller = mock(QuestController.class, CALLS_REAL_METHODS);
        when(qPlayer.getQuestController()).thenReturn(controller);
        QuestProgressFile file = mock(QuestProgressFile.class);
        when(qPlayer.getQuestProgressFile()).thenReturn(file);
        Quest first = mock(Quest.class), second = mock(Quest.class);
        Task active = new Task("active", "mobkilling"), completed = new Task("done", "mobkilling"), last = new Task("last", "mobkilling");
        TaskType type = mock(TaskType.class);
        when(type.getType()).thenReturn("mobkilling");
        when(type.getRegisteredQuests()).thenReturn(List.of(first, second));
        when(first.getTasksOfType("mobkilling")).thenReturn(List.of(active, completed));
        when(second.getTasksOfType("mobkilling")).thenReturn(List.of(last));
        for (Quest quest : List.of(first, second)) {
            when(qPlayer.hasStartedQuest(quest)).thenReturn(true);
            QuestProgress progress = mock(QuestProgress.class);
            when(file.getQuestProgress(quest)).thenReturn(progress);
            for (Task task : quest.getTasksOfType("mobkilling")) {
                TaskProgress taskProgress = mock(TaskProgress.class);
                when(progress.getTaskProgress(task.getId())).thenReturn(taskProgress);
                when(taskProgress.isCompleted()).thenReturn(task == completed);
            }
        }
        assertEquals(List.of(active, last), TaskUtils.getApplicableTasks(mock(Player.class), qPlayer, type)
                .stream().map(TaskUtils.PendingTask::task).toList());
    }
}
