package com.leonardobishop.quests.player;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.QuestsLogger;
import com.leonardobishop.quests.player.questprogressfile.QPlayerPreferences;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.storage.MySqlStorageProvider;
import com.leonardobishop.quests.storage.StorageProvider;
import com.leonardobishop.quests.storage.YamlStorageProvider;
import com.leonardobishop.quests.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QPlayerManager {

    private final Quests plugin;
    private StorageProvider storageProvider;

    public QPlayerManager(Quests plugin) {
        this.plugin = plugin;

        String configuredProvider = plugin.getConfig().getString("options.storage.provider", "yaml");
        if (configuredProvider.equalsIgnoreCase("yaml")) {
            this.storageProvider = new YamlStorageProvider(plugin);
        } else if (configuredProvider.equalsIgnoreCase("mysql")) {
            this.storageProvider = new MySqlStorageProvider(plugin, plugin.getConfig().getConfigurationSection("options.storage.database-settings"));
        } else {
            plugin.getQuestsLogger().warning("No valid storage provider is configured - Quests will use YAML storage as a default");
            this.storageProvider = new YamlStorageProvider(plugin);
        }
        storageProvider.init();
    }

    private final Map<UUID, QPlayer> qPlayers = new ConcurrentHashMap<>();

    /**
     * Gets the QPlayer from a given UUID.
     *
     * @param uuid the uuid
     * @return {@link QPlayer} if they are loaded
     */
    public QPlayer getPlayer(UUID uuid) {
        QPlayer qPlayer = qPlayers.get(uuid);
        if (qPlayer == null) {
            plugin.getQuestsLogger().debug("QPlayer of " + uuid + " is null, but was requested:");
            if (plugin.getQuestsLogger().getServerLoggingLevel() == QuestsLogger.LoggingLevel.DEBUG) {
                Thread.dumpStack();
            }
        }
        return qPlayer;
    }

    /**
     * Unloads and saves the player to disk.
     *
     * @param uuid the uuid of the player
     * @param questProgressFile the quest progress file to save
     */
    public void removePlayer(UUID uuid, QuestProgressFile questProgressFile) {
        plugin.getQuestsLogger().debug("Unloading and saving player " + uuid + ". Main thread: " + Bukkit.isPrimaryThread());
        qPlayers.computeIfPresent(uuid, (mapUUID, qPlayer) -> {
            storageProvider.saveProgressFile(uuid, questProgressFile);
            return null;
        });
    }

    /**
     * Saves the player to disk with a specified {@link QuestProgressFile}.
     *
     * @param uuid the uuid of the player
     * @param questProgressFile the quest progress file to associate with and save
     */
    public void savePlayer(UUID uuid, QuestProgressFile questProgressFile) {
        plugin.getQuestsLogger().debug("Saving player " + uuid + ". Main thread: " + Bukkit.isPrimaryThread());
        storageProvider.saveProgressFile(uuid, questProgressFile);
    }

    /**
     * Saves the player to disk using the {@link QuestProgressFile} associated by the {@link QPlayerManager}
     *
     * @param uuid the uuid of the player
     */
    public void savePlayer(UUID uuid) {
        savePlayer(uuid, getPlayer(uuid).getQuestProgressFile());
    }

    /**
     * Unloads the player without saving to disk.
     *
     * @param uuid the uuid of the player
     */
    public void dropPlayer(UUID uuid) {
        plugin.getQuestsLogger().debug("Dropping player " + uuid + ".");
        qPlayers.remove(uuid);
    }

    public Collection<QPlayer> getQPlayers() {
        return qPlayers.values();
    }

    /**
     * Load the player from disk if they exist, otherwise create a new {@link QuestProgressFile}.
     * This will have no effect if player is already loaded. Can be invoked asynchronously.
     *
     * @param uuid the uuid of the player
     */
    public void loadPlayer(UUID uuid) {
        plugin.getQuestsLogger().debug("Loading player " + uuid + ". Main thread: " + Bukkit.isPrimaryThread());
        qPlayers.computeIfAbsent(uuid, s -> {
            QuestProgressFile questProgressFile = storageProvider.loadProgressFile(uuid);
            return new QPlayer(uuid, questProgressFile, new QPlayerPreferences(null), plugin);
        });
    }
}
