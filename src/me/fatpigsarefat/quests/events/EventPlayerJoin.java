package me.fatpigsarefat.quests.events;

import me.fatpigsarefat.quests.Quests;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class EventPlayerJoin implements Listener {

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        Quests.getPlayerManager().loadPlayer(playerUuid);
        if (Quests.getUpdater().isUpdateReady() && event.getPlayer().hasPermission("quests.admin")) {
            event.getPlayer().sendMessage(Quests.getUpdater().getMessage());
        }
    }

}
