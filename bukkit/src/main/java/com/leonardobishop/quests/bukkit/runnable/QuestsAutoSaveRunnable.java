package com.leonardobishop.quests.bukkit.runnable;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.scheduler.WrappedRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class QuestsAutoSaveRunnable extends WrappedRunnable {

    private final Queue<UUID> queue = new LinkedList<>();
    private final BukkitQuestsPlugin plugin;

    public QuestsAutoSaveRunnable(BukkitQuestsPlugin plugin) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            queue.add(player.getUniqueId());
        }

        this.plugin = plugin;
    }

    @Override
    public void run() {
        UUID player = queue.poll();
        if (player == null) {
            try {
                super.cancel();
            } catch (Exception ignored) {}
            return;
        }

        if (Bukkit.getPlayer(player) != null) {
            plugin.getPlayerManager().savePlayer(player);
        }
    }

}
