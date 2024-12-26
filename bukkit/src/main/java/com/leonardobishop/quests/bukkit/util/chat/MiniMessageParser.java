package com.leonardobishop.quests.bukkit.util.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MiniMessageParser {

    private final MiniMessage miniMessage;

    public MiniMessageParser() {
        Method miniMessageGetter;

        try {
            miniMessageGetter = MiniMessage.class.getMethod("miniMessage");
        } catch (final NoSuchMethodException e) {
            try {
                // For some reason on latest Purpur 1.17.1 there is no MiniMessage#miniMessage method
                // https://github.com/LMBishop/Quests/issues/756
                //noinspection JavaReflectionMemberAccess
                miniMessageGetter = MiniMessage.class.getMethod("get");
            } catch (final NoSuchMethodException e2) {
                throw new IllegalStateException("could not find MiniMessage getter");
            }
        }

        try {
            this.miniMessage = (MiniMessage) miniMessageGetter.invoke(null);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("could not get MiniMessage instance", e);
        }
    }

    public void send(final @NotNull CommandSender who, final @NotNull String message) {
        final Component component = this.miniMessage.deserialize(message);
        who.sendMessage(component);
    }
}
