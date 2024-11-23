package com.leonardobishop.quests.common.storage;

import com.leonardobishop.quests.common.player.QPlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * The StorageProvider interface defines the contract for a storage system that handles the persistence
 * of player data, such as player preferences and quest progress, for specific players identified by their UUIDs.
 * Implementations of this interface are responsible for the actual storage and retrieval of this data.
 */
public interface StorageProvider {

    /**
     * Retrieves the name of this storage provider.
     *
     * @return the name of the storage provider
     */
    @NotNull String getName();

    /**
     * Initializes the storage provider, preparing it for use. This method should be called before any
     * other operations are performed. Initialization may involve setting up connections or loading necessary resources.
     */
    void init() throws IOException;

    /**
     * Shuts down the storage provider, ensuring that any open resources are properly closed and that
     * any pending data is safely stored. This method should be called during the application's shutdown process.
     */
    void shutdown();

    /**
     * Loads the player data associated with the given UUID from the storage.
     *
     * @param uuid the unique identifier of the player whose data is to be loaded
     * @return the {@link QPlayerData} for the player, or null if no data is found for the given UUID
     */
    @Nullable QPlayerData loadPlayerData(final @NotNull UUID uuid);

    /**
     * Saves the given player data to the storage.
     *
     * @param playerData the {@link QPlayerData} object containing the player's data to be saved
     * @return true if the data was successfully saved, false otherwise
     */
    boolean savePlayerData(final @NotNull QPlayerData playerData);

    /**
     * Loads all player data available in the storage.
     *
     * @return a list of {@link QPlayerData} objects
     */
    @NotNull List<QPlayerData> loadAllPlayerData();

    /**
     * Saves all provided player data to the storage.
     *
     * @param allPlayerData a list of {@link QPlayerData} objects to be saved
     * @return true if the data was successfully saved, false otherwise
     */
    default boolean saveAllPlayerData(final @NotNull List<QPlayerData> allPlayerData) {
        Objects.requireNonNull(allPlayerData, "allPlayerData cannot be null");

        // fault check is not needed here as the method
        // saving single player data already handles that,
        // and it's actually the one we need to check

        boolean result = true;

        for (final QPlayerData playerData : allPlayerData) {
            result &= this.savePlayerData(playerData);
        }

        return result;
    }

    /**
     * Compares this storage provider with another to determine if they are similar.
     * Similarity is determined by effectively pointing to the same data source.
     *
     * @param otherProvider another StorageProvider to compare against
     * @return true if the two storage providers are considered similar, false otherwise
     */
    boolean isSimilar(final @NotNull StorageProvider otherProvider);
}
