package com.leonardobishop.quests.bukkit.api.event;

import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerStartTrackQuestEvent extends PlayerQuestEvent {
    private final static HandlerList handlers = new HandlerList();
    private final QPlayer qPlayer;

    public PlayerStartTrackQuestEvent(@NotNull Player who, QPlayer qPlayer) {
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

    public static HandlerList getHandlerList() {
        return handlers;
    }


}
