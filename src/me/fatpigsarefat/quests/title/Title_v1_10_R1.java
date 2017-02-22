package me.fatpigsarefat.quests.title;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;

public class Title_v1_10_R1 implements Title {

	@Override
	public void sendTitle(Player player, String message, String submessage) {
		message = "{\"text\":\"" + message + "\"}";
		PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a(message), 20, 100,
				20);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
		submessage = "{\"text\":\"" + submessage + "\"}";
		PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a(submessage), 20,
				100, 20);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
	}
}
