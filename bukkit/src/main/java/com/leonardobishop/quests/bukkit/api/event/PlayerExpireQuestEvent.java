package com.leonardobishop.quests.bukkit.api.event;

import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerExpireQuestEvent extends PlayerQuestEvent {
    private final static HandlerList handlers = new HandlerList();
    private final QuestProgress questProgress;
    private String questExpireMessage;

    public PlayerExpireQuestEvent(@NotNull Player who, @NotNull QPlayer questPlayer, @NotNull QuestProgress questProgress, String questExpireMessage) {
        super(who, questPlayer);
        this.questProgress = questProgress;
        this.questExpireMessage = questExpireMessage;
    }

    /**
     * @return The quest progress
     */
    public QuestProgress getQuestProgress() {
        return this.questProgress;
    }

    /**
     * @return The message sent to the player
     */
    public String getQuestExpireMessage() {
        return this.questExpireMessage;
    }

    /**
     * @param questExpireMessage The quest expire message
     * @return The quest expire message set
     */
    public String setQuestExpireMessage(String questExpireMessage) {
        return (this.questExpireMessage = questExpireMessage);
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
