package com.leonardobishop.quests.bukkit.util.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class MiniMessageParser {

    private final MiniMessage miniMessage;

    public MiniMessageParser() {
        miniMessage = MiniMessage.miniMessage();
    }

    public void send(CommandSender who, String message) {
        Component component = miniMessage.deserialize(message);
        who.sendMessage(component);
    }

}
