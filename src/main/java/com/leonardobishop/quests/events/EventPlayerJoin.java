package com.leonardobishop.quests.events;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.obj.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class EventPlayerJoin implements Listener {

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        Quests.getPlayerManager().loadPlayer(playerUuid);
        if (Quests.getInstance().getDescription().getVersion().contains("beta") && event.getPlayer().hasPermission("quests.admin")) {
            event.getPlayer().sendMessage(Messages.BETA_REMINDER.getMessage());
        }
        if (Quests.getUpdater().isUpdateReady() && event.getPlayer().hasPermission("quests.admin")) {
            event.getPlayer().sendMessage(Quests.getUpdater().getMessage());
        }
    }

}
