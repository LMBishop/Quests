package com.leonardobishop.quests.bukkit.util.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class MiniMessageParser {

    private final BukkitAudiences adventure;
    private final MiniMessage miniMessage;

    public MiniMessageParser(Plugin plugin) {
        adventure = BukkitAudiences.create(plugin);
        miniMessage = MiniMessage.miniMessage();
    }

    public void send(CommandSender who, String message) {
        Audience audience = adventure.sender(who);
        Component component = miniMessage.deserialize(message);
        audience.sendMessage(component);
    }

}
