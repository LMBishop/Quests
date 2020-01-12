package com.leonardobishop.quests.api.events;

import com.leonardobishop.quests.player.QPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerQuestEvent extends PlayerEvent {
    private final QPlayer questPlayer;

    public PlayerQuestEvent(@NotNull Player who, @NotNull QPlayer questPlayer) {
        super(who);
        this.questPlayer = questPlayer;
    }

    public QPlayer getQuestPlayer() {
        return this.questPlayer;
    }
}
