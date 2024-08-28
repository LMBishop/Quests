package com.leonardobishop.quests.bukkit.util.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class MiniMessageParser {

    private final MiniMessage miniMessage;

    public MiniMessageParser() {
        this.miniMessage = MiniMessage.miniMessage();
    }

    public void send(final @NotNull CommandSender who, final @NotNull String message) {
        final Component component = this.miniMessage.deserialize(message);
        who.sendMessage(component);
    }
}
