//package me.fatpigsarefat.quests.title;
//
//import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
//import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
//import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
//import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
//import org.bukkit.entity.Player;
//
//public class Title_v1_8_R3 implements Title {
//
//    @Override
//    public void sendTitle(Player player, String message, String submessage) {
//	message = "{\"text\":\"" + message + "\"}";
//	PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a(message), 10, 100,
//			10);
//	((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
//	submessage = "{\"text\":\"" + submessage + "\"}";
//	PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a(submessage), 10,
//		100, 10);
//	((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
//    }
//}
