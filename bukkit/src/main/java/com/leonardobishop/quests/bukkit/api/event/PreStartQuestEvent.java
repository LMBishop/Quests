package com.leonardobishop.quests.bukkit.api.event;

import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PreStartQuestEvent extends PlayerQuestEvent {

    private final static HandlerList handlers = new HandlerList();
    private QuestStartResult questStartResult;
    private String questResultMessage;

    public PreStartQuestEvent(@NotNull Player who, @NotNull QPlayer questPlayer, String questResultMessage, @NotNull QuestStartResult questStartResult) {
        super(who, questPlayer);
        this.questStartResult = questStartResult;
        this.questResultMessage = questResultMessage;
    }

    public QuestStartResult getQuestStartResult() {
        return this.questStartResult;
    }

    public QuestStartResult setQuestStartResult(QuestStartResult questStartResult) {
        return (this.questStartResult = questStartResult);
    }

    /**
     * @return The message sent to the player of the result of the quest
     * <p>
     * For {@link QuestStartResult#QUEST_SUCCESS} please use {@link PlayerStartQuestEvent}
     */
    public String getQuestResultMessage() {
        return this.questResultMessage;
    }

    /**
     * @param questResultMessage The quest result message
     * @return The quest result message set
     * <p>
     * For {@link QuestStartResult#QUEST_SUCCESS} please use {@link PlayerStartQuestEvent}
     */
    public String setQuestResultMessage(String questResultMessage) {
        return (this.questResultMessage = questResultMessage);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
