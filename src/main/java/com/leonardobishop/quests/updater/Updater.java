package com.leonardobishop.quests.updater;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.obj.Messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

public class Updater {
 
    private static final int PROJECT_ID = 23696;

    private final String installedVersion;
    private final Quests plugin;

    private String returnedVersion;
    private URL api;
    private boolean updateReady;
    private long lastCheck;

    public Updater(Quests plugin) {
        this.plugin = plugin;
        this.installedVersion = plugin.getDescription().getVersion();
        try {
            this.api = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + PROJECT_ID);
        } catch (MalformedURLException ignored) {
            // shit + fan
        }
    }

    public String getLink() {
        return "https://www.spigotmc.org/resources/" + PROJECT_ID;
    }
 
    public boolean check() {
        if (lastCheck != 0 && TimeUnit.MINUTES.convert(System.currentTimeMillis() - lastCheck, TimeUnit.MILLISECONDS) < 10) {
            return updateReady;
        }
        try {
            lastCheck = System.currentTimeMillis();
            URLConnection con = api.openConnection();
            returnedVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (!returnedVersion.equals(installedVersion)) {
                plugin.getQuestsLogger().info("A new version " + returnedVersion + " was found on Spigot (your version: " + installedVersion + "). Please update me! <3 - Link: " + getLink());
                updateReady = true;
            }
        } catch (IOException e) {
            plugin.getQuestsLogger().warning("Failed to check for updates. You can check manually at " + getLink());
            // probably offline
        }
        return false;
    }

    public boolean isUpdateReady() {
        return updateReady;
    }

    public String getMessage() {
        return Messages.QUEST_UPDATER.getMessage().replace("{newver}", returnedVersion).replace("{oldver}", installedVersion).replace("{link}", getLink());
    }
}