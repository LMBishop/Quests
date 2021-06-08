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
        try {
            storageProvider.init();
        } catch (Exception ignored) {
            plugin.getQuestsLogger().severe("An error occurred initialising the storage provider.");
        }
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
     * Unloads and schedules a save for the player. See {@link QPlayerManager#savePlayer(UUID)}
     *
     * @param uuid the uuid of the player
     */
    public void removePlayer(UUID uuid) {
        plugin.getQuestsLogger().debug("Unloading and saving player " + uuid + ". Main thread: " + Bukkit.isPrimaryThread());
        qPlayers.computeIfPresent(uuid, (mapUUID, qPlayer) -> {
            savePlayer(uuid);
            return null;
        });
    }

    /**
     * Schedules a save for the player with the {@link QuestProgressFile} associated by the {@link QPlayerManager}.
     * The modified status of the progress file will be reset.
     *
     * @param uuid the uuid of the player
     */
    public void savePlayer(UUID uuid) {
        QPlayer qPlayer = getPlayer(uuid);
        if (qPlayer == null) return;
        savePlayer(uuid, qPlayer.getQuestProgressFile());
    }

    /**
     * Schedules a save for the player with a specified {@link QuestProgressFile}. The modified status of the
     * specified progress file will be reset.
     *
     * @param uuid the uuid of the player
     * @param originalProgressFile the quest progress file to associate with and save
     */
    public void savePlayer(UUID uuid, QuestProgressFile originalProgressFile) {
        QuestProgressFile clonedProgressFile = new QuestProgressFile(originalProgressFile);
        originalProgressFile.resetModified();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> save(uuid, clonedProgressFile));
    }

    /**
     * Immediately saves the player with the {@link QuestProgressFile} associated by the {@link QPlayerManager},
     * on the same thread. The modified status of the specified progress file is not changed.
     *
     * @param uuid the uuid of the player
     */
    public void savePlayerSync(UUID uuid) {
        QPlayer qPlayer = getPlayer(uuid);
        if (qPlayer == null) return;
        savePlayerSync(uuid, qPlayer.getQuestProgressFile());
    }

    /**
     * Immediately saves the player with a specified {@link QuestProgressFile}, on the same thread. The modified status
     * of the specified progress file is not changed.
     *
     * @param uuid the uuid of the player
     * @param questProgressFile the quest progress file to associate with and save
     */
    public void savePlayerSync(UUID uuid, QuestProgressFile questProgressFile) {
        save(uuid, questProgressFile);
    }

    private void save(UUID uuid, QuestProgressFile questProgressFile) {
        plugin.getQuestsLogger().debug("Saving player " + uuid + ". Main thread: " + Bukkit.isPrimaryThread());
        storageProvider.saveProgressFile(uuid, questProgressFile);
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
     * Load the player if they exist, otherwise create a new {@link QuestProgressFile}.
     * This will have no effect if player is already loaded. Can be invoked asynchronously.
     *
     * @param uuid the uuid of the player
     */
    public void loadPlayer(UUID uuid) {
        plugin.getQuestsLogger().debug("Loading player " + uuid + ". Main thread: " + Bukkit.isPrimaryThread());
        qPlayers.computeIfAbsent(uuid, s -> {
            QuestProgressFile questProgressFile = storageProvider.loadProgressFile(uuid);
            if (questProgressFile == null) return null;
            return new QPlayer(uuid, questProgressFile, new QPlayerPreferences(null), plugin);
        });
    }

    /**
     * Gets the current storage provider which loads and saves players.
     *
     * @return {@link StorageProvider}
     */
    public StorageProvider getStorageProvider() {
        return storageProvider;
    }
}
