package com.leonardobishop.quests.bungee.listener;

import com.leonardobishop.quests.bungee.BungeeQuestsPlugin;
import com.leonardobishop.quests.common.enums.PluginMessagingChannels;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PluginMessageListener implements Listener {

    private final BungeeQuestsPlugin plugin;

    public PluginMessageListener(BungeeQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (!e.getTag().equals(PluginMessagingChannels.QUESTS_LOCKS_CHANNEL)) {
            return;
        }
        e.setCancelled(true);
        if (!(e.getSender() instanceof Server) || !(e.getReceiver() instanceof ProxiedPlayer)) {
            return;
        }
        Server sender = (Server) e.getSender();

        ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
        DataInputStream in = new DataInputStream(stream);
        try {
            String request = in.readUTF();
            UUID uuid = UUID.fromString(in.readUTF());

            if (request.equals("acquireLock")) {
                plugin.getDataLockManager().acquireLock(sender.getInfo().getName(), uuid,
                        () -> sendMessage(sender.getInfo().getName(), "lockAcquired", uuid));
            }
        } catch (Exception ex) {
            plugin.getLogger().warning("Could not parse plugin message:");
            ex.printStackTrace();
        }
    }

    public void sendMessage(String server, String request, UUID who) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(request);
            out.writeUTF(who.toString());
        } catch (IOException ex) {
            plugin.getLogger().warning("Could not write plugin message:");
            ex.printStackTrace();
        }

        ServerInfo serverInfo = plugin.getProxy().getServerInfo(server);
        if (serverInfo != null) serverInfo.sendData(PluginMessagingChannels.QUESTS_LOCKS_CHANNEL, stream.toByteArray());
    }

}
