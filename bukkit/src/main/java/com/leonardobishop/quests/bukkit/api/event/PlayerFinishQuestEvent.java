package com.leonardobishop.quests.bukkit.api.event;

import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerFinishQuestEvent extends PlayerQuestEvent {
    private final static HandlerList handlers = new HandlerList();
    private final QuestProgress questProgress;
    private String questFinishMessage;

    public PlayerFinishQuestEvent(@NotNull Player who, @NotNull QPlayer questPlayer, @NotNull QuestProgress questProgress, String questFinishMessage) {
        super(who, questPlayer);
        this.questProgress = questProgress;
        this.questFinishMessage = questFinishMessage;
    }

    /**
     * @return The quest progress
     */
    public QuestProgress getQuestProgress() {
        return this.questProgress;
    }

    /**
     * @return The message sent to the player that finish the quest
     */
    public String getQuestFinishMessage() {
        return this.questFinishMessage;
    }

    /**
     * @param questFinishMessage The quest finish message
     * @return The quest finish message set
     */
    public String setQuestFinishMessage(String questFinishMessage) {
        return (this.questFinishMessage = questFinishMessage);
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
