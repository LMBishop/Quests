package com.leonardobishop.quests.common.questcontroller;

import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.util.Modern;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * The quests controller dictates how the plugin should act and acts as a bridge between a player
 * and their progress file, interpreting the progress file and mutating it on certain events.
 */
@Modern(type = Modern.Type.FULL)
@NullMarked
public interface QuestController {

    @Contract(pure = true)
    String getName();

    @Contract(pure = true)
    QuestStartResult canPlayerStartQuest(QPlayer qPlayer, Quest quest);

    @Contract(pure = true)
    boolean hasPlayerStartedQuest(QPlayer qPlayer, Quest quest);

    QuestStartResult startQuestForPlayer(QPlayer qPlayer, Quest quest);

    boolean completeQuestForPlayer(QPlayer qPlayer, Quest quest);

    boolean cancelQuestForPlayer(QPlayer qPlayer, Quest quest);

    boolean expireQuestForPlayer(QPlayer qPlayer, Quest quest);

    void trackQuestForPlayer(QPlayer qPlayer, @Nullable Quest quest);
}
