package com.leonardobishop.quests.bukkit.api.event;

import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerCancelQuestEvent extends PlayerQuestEvent {
    private final static HandlerList handlers = new HandlerList();
    private final QuestProgress questProgress;
    private String questCancelMessage;

    public PlayerCancelQuestEvent(@NotNull Player who, @NotNull QPlayer questPlayer, @NotNull QuestProgress questProgress, String questCancelMessage) {
        super(who, questPlayer);
        this.questProgress = questProgress;
        this.questCancelMessage = questCancelMessage;
    }

    /**
     * @return The quest progress
     */
    public QuestProgress getQuestProgress() {
        return this.questProgress;
    }

    /**
     * @return The message sent to the player that cancel the quest
     */
    public String getQuestCancelMessage() {
        return this.questCancelMessage;
    }

    /**
     * @param questCancelMessage The quest cancel message
     * @return The quest cancel message set
     */
    public String setQuestCancelMessage(String questCancelMessage) {
        return (this.questCancelMessage = questCancelMessage);
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
