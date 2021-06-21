package com.leonardobishop.quests.bungee.lock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

//TODO https://github.com/LMBishop/Quests/issues/180
public class DataLockManager {

    private final Map<UUID, String> locks = new HashMap<>();
    private final Map<UUID, LinkedList<QueuedServer>> queue = new HashMap<>();

    public synchronized boolean hasLock(String server, UUID who) {
        String lockedServer = locks.get(who);
        if (lockedServer == null) {
            return false;
        } else {
            return lockedServer.equals(server);
        }
    }

    public synchronized void acquireLock(String server, UUID who, Runnable callback) {
        String lockedServer = locks.get(who);
        if (lockedServer == null) {
            locks.put(who, server);
            callback.run();
        } else if (lockedServer.equals(server)) {
            callback.run();
        }
        CompletableFuture<Void> future = new CompletableFuture<>();
        QueuedServer queuedServer = new QueuedServer(callback, server);
        queue.compute(who, (uuid, list) -> {
            if (list == null) {
                LinkedList<QueuedServer> newList = new LinkedList<>();
                newList.add(queuedServer);
                return newList;
            } else {
                list.add(queuedServer);
            }
            return list;
        });
    }

    public synchronized void releaseLock(String server, UUID who) {
        String lockedServer = locks.get(who);
        if (lockedServer == null || lockedServer.equals(server)) {
            LinkedList<QueuedServer> queuedServers = queue.get(who);
            QueuedServer nextServer = queuedServers.poll();
            if (nextServer != null) {
                locks.put(who, nextServer.getServer());
                nextServer.getCallback().run();
            } else {
                locks.remove(who);
            }
        } else {
            LinkedList<QueuedServer> queuedServers = queue.get(who);
            QueuedServer queuedServer = null;
            for (QueuedServer qs : queuedServers) {
                if (qs.getServer().equals(server)) queuedServer = qs;
            }
            if (queuedServer != null) queuedServers.remove(queuedServer);
        }
    }

}
