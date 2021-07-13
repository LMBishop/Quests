package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.common.player.QPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class StartedCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public StartedCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            player.sendMessage(Messages.COMMAND_DATA_NOT_LOADED.getMessage());
            return;
        }
        plugin.getMenuController().openStartedQuests(qPlayer);
        return;
    }

    @Override
    public @Nullable String getPermission() {
        return null;
    }
}
