package com.leonardobishop.quests.bungee.lock;

public class QueuedServer {

    private Runnable callback;
    private String server;

    public QueuedServer(Runnable callback, String server) {
        this.callback = callback;
        this.server = server;
    }

    public Runnable getCallback() {
        return callback;
    }

    public String getServer() {
        return server;
    }

}
