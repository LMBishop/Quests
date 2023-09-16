package com.leonardobishop.quests.bukkit.hook.actionbar;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBar_Spigot implements QuestsActionBar {
    @SuppressWarnings("deprecation")
    @Override
    public void sendActionBar(Player player, String title) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(title));
    }
}
