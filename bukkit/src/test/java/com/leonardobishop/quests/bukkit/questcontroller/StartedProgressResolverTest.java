package com.leonardobishop.quests.bukkit.questcontroller;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.*;
import com.leonardobishop.quests.common.player.questprogressfile.*;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.questcontroller.QuestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartedProgressResolverTest {
    private BukkitQuestsPlugin plugin;
    private BukkitQuestsConfig config;
    private NormalQuestController controller;
    private QuestProgressFile file;
    private QPlayer player;
    private Quest quest;
    private QuestProgress progress;

    @BeforeEach void setup() {
        plugin = mock(BukkitQuestsPlugin.class, RETURNS_DEEP_STUBS);
        config = mock(BukkitQuestsConfig.class);
        when(plugin.getQuestsConfig()).thenReturn(config);
        controller = spy(new NormalQuestController(plugin));
        UUID id = UUID.randomUUID();
        file = new QuestProgressFile(plugin, id);
        player = new QPlayer(plugin, new QPlayerData(id, mock(QPlayerPreferences.class), file), controller);
        quest = new Quest.Builder("manual").build();
        progress = new QuestProgress(plugin, "manual", id, true, 0L, false, false, 0L);
        file.addQuestProgress(progress);
    }

    @Test void manualProgressIsLiveAndCompletedDoesNotChangeStartedSemantics() {
        var resolver = controller.startedProgressResolver(player);
        assertSame(progress, resolver.apply(quest));
        progress.setCompleted(true);
        assertSame(progress, resolver.apply(quest));
        progress.setStarted(false);
        assertNull(resolver.apply(quest));
        progress.setStarted(true);
        assertSame(progress, resolver.apply(quest));
        file.clear();
        assertNull(resolver.apply(quest));
        assertNull(file.getQuestProgressOrNull(quest));
        verify(controller, never()).canPlayerStartQuest(any(), any());
    }

    @Test void globallyAutomaticQuestsKeepTheEligibilityPolicyAndGenerateProgressOnlyWhenAllowed() {
        when(config.getBoolean("options.quest-autostart")).thenReturn(true);
        file.clear();
        doReturn(QuestStartResult.QUEST_LOCKED).when(controller).canPlayerStartQuest(player, quest);
        assertNull(controller.startedProgressResolver(player).apply(quest));
        assertNull(file.getQuestProgressOrNull(quest));
        doReturn(QuestStartResult.QUEST_SUCCESS).when(controller).canPlayerStartQuest(player, quest);
        assertNotNull(controller.startedProgressResolver(player).apply(quest));
    }

    @Test void perQuestAutostartKeepsTheEligibilityPolicy() {
        Quest automatic = new Quest.Builder("auto").withAutoStartEnabled(true).build();
        doReturn(QuestStartResult.QUEST_COOLDOWN).when(controller).canPlayerStartQuest(player, automatic);
        var resolver = controller.startedProgressResolver(player);
        assertNull(resolver.apply(automatic));
        doReturn(QuestStartResult.QUEST_SUCCESS).when(controller).canPlayerStartQuest(player, automatic);
        assertNotNull(resolver.apply(automatic));
    }

    @Test void nextTraversalReadsReloadedGlobalAutostart() {
        progress.setStarted(false);
        assertNull(controller.startedProgressResolver(player).apply(quest));
        when(config.getBoolean("options.quest-autostart")).thenReturn(true);
        doReturn(QuestStartResult.QUEST_SUCCESS).when(controller).canPlayerStartQuest(player, quest);
        assertSame(progress, controller.startedProgressResolver(player).apply(quest));
    }

    @Test void customPlayerControllerRetainsItsOwnPolicy() {
        QuestController custom = mock(QuestController.class, CALLS_REAL_METHODS);
        player.setQuestController(custom);
        when(custom.hasPlayerStartedQuest(player, quest)).thenReturn(false, true);
        var resolver = custom.startedProgressResolver(player);
        assertNull(resolver.apply(quest));
        assertSame(progress, resolver.apply(quest));
    }

    @Test void normalSubclassOverrideIsNotBypassed() {
        NormalQuestController subclass = new NormalQuestController(plugin) {
            @Override public boolean hasPlayerStartedQuest(QPlayer ignored, Quest ignoredQuest) { return false; }
        };
        player.setQuestController(subclass);
        assertNull(subclass.startedProgressResolver(player).apply(quest));
    }
}
