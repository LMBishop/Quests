package com.leonardobishop.quests.common.player;

import com.leonardobishop.quests.common.logger.QuestsLogger;
import com.leonardobishop.quests.common.storage.StorageProvider;
import com.leonardobishop.quests.common.plugin.Quests;
import com.leonardobishop.quests.common.questcontroller.QuestController;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The QPlayerManager is responsible for keeping a reference to all players on the server and is used to
 * obtain an instance of a player, load new players and save current players.
 */
public class QPlayerManager {

    private final Map<UUID, QPlayer> qPlayers = new ConcurrentHashMap<>();
    private final Quests plugin;
    private final StorageProvider storageProvider;
    private QuestController activeQuestController;

    public QPlayerManager(Quests plugin, StorageProvider storageProvider, QuestController questController) {
        this.plugin = plugin;
        this.storageProvider = storageProvider;
        this.activeQuestController = questController;
    }

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
        plugin.getQuestsLogger().debug("Unloading and saving player " + uuid + ".");
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
        plugin.getScheduler().doAsync(() -> save(uuid, clonedProgressFile));
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
        plugin.getQuestsLogger().debug("Saving player " + uuid + ".");
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
        plugin.getQuestsLogger().debug("Loading player " + uuid + ".");
        qPlayers.computeIfAbsent(uuid, s -> {
            QuestProgressFile questProgressFile = storageProvider.loadProgressFile(uuid);
            if (questProgressFile == null) return null;
            return new QPlayer(plugin, uuid, new QPlayerPreferences(null), questProgressFile, activeQuestController);
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

    public QuestController getActiveQuestController() {
        return activeQuestController;
    }

    public void setActiveQuestController(QuestController activeQuestController) {
        this.activeQuestController = activeQuestController;
        for (QPlayer qPlayer : qPlayers.values()) {
            qPlayer.setQuestController(activeQuestController);
        }
    }
}
