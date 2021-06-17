package com.leonardobishop.quests.bukkit.api.event;

import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerStartQuestEvent extends PlayerQuestEvent {

    private final static HandlerList handlers = new HandlerList();
    private final QuestProgress questProgress;
    private String questStartMessage;

    public PlayerStartQuestEvent(@NotNull Player who, @NotNull QPlayer questPlayer, @NotNull QuestProgress questProgress, String questStartMessage) {
        super(who, questPlayer);
        this.questProgress = questProgress;
        this.questStartMessage = questStartMessage;
    }

    /**
     * @return The quest progress
     */
    public QuestProgress getQuestProgress() {
        return this.questProgress;
    }

    /**
     * @return The message sent to the player that start the quest
     */
    public String getQuestStartMessage() {
        return this.questStartMessage;
    }

    /**
     * @param questStartMessage The quest start message
     * @return The quest start message set
     */
    public String setQuestStartMessage(String questStartMessage) {
        return (this.questStartMessage = questStartMessage);
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
