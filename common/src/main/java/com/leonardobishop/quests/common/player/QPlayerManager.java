package com.leonardobishop.quests.common.player;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.plugin.Quests;
import com.leonardobishop.quests.common.questcontroller.QuestController;
import com.leonardobishop.quests.common.storage.StorageProvider;
import com.leonardobishop.quests.common.util.Modern;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The QPlayerManager is responsible for keeping a reference to all players on the server and is used to
 * obtain an instance of a player, load new players and save current players.
 */
@Modern(type = Modern.Type.FULL)
@NullMarked
public final class QPlayerManager {

    private final Quests plugin;
    private final StorageProvider storageProvider;
    private final Map<UUID, QPlayer> qPlayerMap;
    private QuestController activeQuestController;

    public QPlayerManager(final Quests plugin, final StorageProvider storageProvider, final QuestController questController) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        this.storageProvider = Objects.requireNonNull(storageProvider, "storageProvider cannot be null");
        this.activeQuestController = Objects.requireNonNull(questController, "questController cannot be null");
        this.qPlayerMap = new ConcurrentHashMap<>();
    }

    /**
     * Gets the QPlayer from a given UUID.
     *
     * @param uuid the uuid
     * @return {@link QPlayer} if they are loaded, otherwise null
     */
    public @Nullable QPlayer getPlayer(final UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");

//        QPlayer qPlayer = qPlayers.get(uuid);
//        if (qPlayer == null) {
//            plugin.getQuestsLogger().debug("QPlayer of " + uuid + " is null, but was requested:");
//            if (plugin.getQuestsLogger().getServerLoggingLevel() == QuestsLogger.LoggingLevel.DEBUG) {
//                Thread.dumpStack();
//            }
//        }
        return this.qPlayerMap.get(uuid);
    }

    /**
     * Unloads and schedules a save for the player. See {@link QPlayerManager#savePlayer(UUID)}
     *
     * @param uuid the uuid of the player
     */
    public void removePlayer(final UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");

        this.plugin.getQuestsLogger().debug("Unloading and saving player " + uuid + "...");
        final CompletableFuture<Void> future = this.savePlayer(uuid);
        future.thenAccept(unused -> this.qPlayerMap.remove(uuid));
    }

    /**
     * Schedules a save for the player with the {@link QuestProgressFile} associated by the {@link QPlayerManager}.
     * The modified status of the progress file will be reset.
     *
     * @param uuid the uuid of the player
     * @return completable future
     */
    public CompletableFuture<@Nullable Void> savePlayer(final UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");

        final QPlayer qPlayer = this.getPlayer(uuid);
        if (qPlayer == null) {
            return CompletableFuture.completedFuture(null);
        }

        return this.savePlayer(qPlayer.getPlayerData());
    }

    /**
     * Schedules a save for the player with a specified {@link QuestProgressFile}. The modified status of the
     * specified progress file will be reset.
     */
    public CompletableFuture<@Nullable Void> savePlayer(final QPlayerData playerData) {
        Objects.requireNonNull(playerData, "playerData cannot be null");

        final CompletableFuture<@Nullable Void> future = new CompletableFuture<>();
        final QPlayerData clonedPlayerData = new QPlayerData(playerData);
        playerData.setModified(false);

        this.plugin.getScheduler().doAsync(() -> {
            this.save(clonedPlayerData);
            future.complete(null);
        });

        return future;
    }

    /**
     * Immediately saves the player with the {@link QuestProgressFile} associated by the {@link QPlayerManager},
     * on the same thread. The modified status of the specified progress file is not changed.
     *
     * @param uuid the uuid of the player
     */
    public void savePlayerSync(final UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");

        final QPlayer qPlayer = this.getPlayer(uuid);
        if (qPlayer == null) {
            return;
        }

        this.savePlayerSync(qPlayer.getPlayerData());
    }

    /**
     * Immediately saves the player with a specified {@link QuestProgressFile}, on the same thread. The modified status
     * of the specified progress file is not changed.
     */
    public void savePlayerSync(final QPlayerData playerData) {
        this.save(playerData);
    }

    private void save(final QPlayerData playerData) {
        Objects.requireNonNull(playerData, "playerData cannot be null");

        final String uuidString = playerData.playerUUID().toString();
        this.plugin.getQuestsLogger().debug("Saving player " + uuidString + "...");

        if (this.storageProvider.savePlayerData(playerData)) {
            this.plugin.getQuestsLogger().debug("Quest progress file saved for player " + uuidString + ".");
        } else {
            this.plugin.getQuestsLogger().severe("Failed to save player " + uuidString + "!");
        }
    }

    /**
     * Unloads the player without saving to disk.
     *
     * @param uuid the uuid of the player
     */
    public void dropPlayer(final UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");

        this.plugin.getQuestsLogger().debug("Dropping player " + uuid + ".");
        this.qPlayerMap.remove(uuid);
    }

    /**
     * Gets all QPlayers loaded on the server
     *
     * @return immutable collection of quest players
     */
    public @UnmodifiableView Collection<QPlayer> getQPlayers() {
        return Collections.unmodifiableCollection(this.qPlayerMap.values());
    }

    /**
     * Load the player if they exist, otherwise create a new {@link QuestProgressFile}.
     * This will have no effect if player is already loaded.
     *
     * @param uuid the uuid of the player
     * @return completable future with the loaded player, or null if there was an error
     */
    public CompletableFuture<@Nullable QPlayer> loadPlayer(final UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");

        final String uuidString = uuid.toString();
        this.plugin.getQuestsLogger().debug("Loading player " + uuidString + "...");
        final CompletableFuture<@Nullable QPlayer> future = new CompletableFuture<>();

        this.plugin.getScheduler().doAsync(() -> {
            final QPlayerData playerData = this.storageProvider.loadPlayerData(uuid);

            if (playerData == null) {
                this.plugin.getQuestsLogger().debug("A problem occurred trying loading player " + uuidString + "; quest progress file is null.");
                future.complete(null);
                return;
            }

            final QPlayer qPlayer = new QPlayer(this.plugin, playerData, this.activeQuestController);
            this.qPlayerMap.putIfAbsent(uuid, qPlayer);

            this.plugin.getQuestsLogger().debug("Quest progress file loaded for player " + uuidString + ".");
            future.complete(qPlayer);
        });

        return future;
    }

    /**
     * Gets the current storage provider which loads and saves players.
     *
     * @return {@link StorageProvider}
     */
    public StorageProvider getStorageProvider() {
        return this.storageProvider;
    }

    public QuestController getActiveQuestController() {
        return this.activeQuestController;
    }

    public void setActiveQuestController(final QuestController activeQuestController) {
        this.activeQuestController = Objects.requireNonNull(activeQuestController, "activeQuestController cannot be null");

        for (final QPlayer qPlayer : this.qPlayerMap.values()) {
            qPlayer.setQuestController(activeQuestController);
        }
    }
}
