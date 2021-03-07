package com.leonardobishop.quests.api.events;

import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerStopTrackQuestEvent extends PlayerQuestEvent {

    private final static HandlerList handlers = new HandlerList();
    private final QPlayer qPlayer;

    public PlayerStopTrackQuestEvent(@NotNull Player who, QPlayer qPlayer) {
        super(who, qPlayer);
        this.qPlayer = qPlayer;
    }

    public QPlayer getQPlayer() {
        return qPlayer;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
