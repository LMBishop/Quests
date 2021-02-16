package com.leonardobishop.quests.api.events;

import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerStopTrackQuestEvent extends PlayerEvent {

    private final static HandlerList handlers = new HandlerList();
    private final QuestProgressFile questProgressFile;

    public PlayerStopTrackQuestEvent(@NotNull Player who, QuestProgressFile questProgressFile) {
        super(who);
        this.questProgressFile = questProgressFile;
    }

    public QuestProgressFile getQuestProgressFile() {
        return questProgressFile;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
