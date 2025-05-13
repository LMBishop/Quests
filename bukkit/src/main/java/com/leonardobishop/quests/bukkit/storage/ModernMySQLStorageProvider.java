package com.leonardobishop.quests.bukkit.storage;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.QPlayerData;
import com.leonardobishop.quests.common.player.QPlayerPreferences;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.storage.StorageProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

public final class ModernMySQLStorageProvider implements StorageProvider {

    // Table creation SQL
    private static final String CREATE_TABLE_QUEST_PROGRESS =
            "CREATE TABLE IF NOT EXISTS `{prefix}quest_progress` (" +
                    " `uuid`              VARCHAR(36)  NOT NULL," +
                    " `quest_id`          VARCHAR(50)  NOT NULL," +
                    " `started`           BOOL         NOT NULL," +
                    " `started_date`      BIGINT       NOT NULL," +
                    " `completed`         BOOL         NOT NULL," +
                    " `completed_before`  BOOL         NOT NULL," +
                    " `completion_date`   BIGINT       NOT NULL," +
                    " PRIMARY KEY (`uuid`, `quest_id`));";
    private static final String CREATE_TABLE_TASK_PROGRESS =
            "CREATE TABLE IF NOT EXISTS `{prefix}task_progress` (" +
                    " `uuid`       VARCHAR(36)  NOT NULL," +
                    " `quest_id`   VARCHAR(50)  NOT NULL," +
                    " `task_id`    VARCHAR(50)  NOT NULL," +
                    " `completed`  BOOL         NOT NULL," +
                    " `progress`   VARCHAR(64)  NULL," +
                    " `data_type`  VARCHAR(10)  NULL," +
                    " PRIMARY KEY (`uuid`, `quest_id`, `task_id`));";
    private static final String CREATE_TABLE_PLAYER_PREFERENCES =
            "CREATE TABLE IF NOT EXISTS `{prefix}player_preferences` (" +
                    " `uuid`           CHAR(36)      NOT NULL," +
                    " `preference_id`  VARCHAR(255)  NOT NULL," +
                    " `value`          VARCHAR(64)   NULL," +
                    " `data_type`      VARCHAR(10)   NULL," +
                    " PRIMARY KEY (`uuid`, `preference_id`));";
    private static final String CREATE_TABLE_DATABASE_INFORMATION =
            "CREATE TABLE IF NOT EXISTS `{prefix}database_information` (" +
                    " `key`    VARCHAR(255)  NOT NULL," +
                    " `value`  VARCHAR(255)  NOT NULL," +
                    " PRIMARY KEY (`key`));";

    // Selection SQL
    private static final String SELECT_PLAYER_QUEST_PROGRESS =
            "SELECT quest_id, started, started_date, completed, completed_before, completion_date FROM `{prefix}quest_progress` WHERE uuid = ?;";
    private static final String SELECT_PLAYER_TASK_PROGRESS =
            "SELECT quest_id, task_id, completed, progress, data_type FROM `{prefix}task_progress` WHERE uuid = ?;";
    private static final String SELECT_UUID_LIST =
            "SELECT DISTINCT uuid FROM `{prefix}quest_progress`;";

    // Insertion SQL
    private static final String INSERT_PLAYER_QUEST_PROGRESS =
            "INSERT INTO `{prefix}quest_progress` (uuid, quest_id, started, started_date, completed, completed_before, completion_date) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE started = ?, started_date = ?, completed = ?, completed_before = ?, completion_date = ?";
    private static final String INSERT_PLAYER_TASK_PROGRESS =
            "INSERT INTO `{prefix}task_progress` (uuid, quest_id, task_id, completed, progress, data_type) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE completed = ?, progress = ?, data_type = ?";

    private static final Map<String, Object> ADDITIONAL_PROPERTIES = new HashMap<>() {{
        this.put("cachePrepStmts", true);
        this.put("prepStmtCacheSize", 250);
        this.put("prepStmtCacheSqlLimit", 2048);
        this.put("useServerPrepStmts", true);
        this.put("useLocalSessionState", true);
        this.put("rewriteBatchedStatements", true);
        this.put("cacheResultSetMetadata", true);
        this.put("cacheServerConfiguration", true);
        this.put("elideSetAutoCommits", true);
        this.put("maintainTimeStats", false);
    }};

    private final BukkitQuestsPlugin plugin;
    private final ConfigurationSection config;

    private HikariDataSource ds;
    private Function<String, String> prefixer;
    private boolean validateQuests;
    private boolean fault;

    public ModernMySQLStorageProvider(final @NotNull BukkitQuestsPlugin plugin, final @Nullable ConfigurationSection config) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        this.config = Objects.requireNonNullElseGet(config, YamlConfiguration::new);
        this.fault = true;
    }

    @Override
    public @NotNull String getName() {
        return "mysql";
    }

    @Override
    public void init() throws IOException {
        // initialize hikari config and set pool name
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("quests-hikari");

        // set jdbc url
        final String address = this.config.getString("network.address", "localhost:3306");
        final String database = this.config.getString("network.database", "minecraft");
        final String jdbcUrl = "jdbc:mysql://" + address + "/" + database;
        hikariConfig.setJdbcUrl(jdbcUrl);

        // set username
        final String username = this.config.getString("network.username", "root");
        hikariConfig.setUsername(username);

        // set password
        final String password = this.config.getString("network.password");
        hikariConfig.setPassword(password);

        // set pool size related properties
        final int minIdle = this.config.getInt("connection-pool-settings.minimum-idle", 8);
        final int maxPoolSize = this.config.getInt("connection-pool-settings.maximum-pool-size", 8);
        hikariConfig.setMinimumIdle(minIdle);
        hikariConfig.setMaximumPoolSize(maxPoolSize);

        // set pool timeouts related properties
        final long connectionTimeoutMs = this.config.getLong("connection-pool-settings.connection-timeout", 5000L);
        final long idleTimeoutMs = this.config.getLong("connection-pool-settings.idle-timeout", 600000L);
        final long keepaliveTimeMs = this.config.getLong("connection-pool-settings.keepalive-time", 0L);
        final long maxLifetimeMs = this.config.getLong("connection-pool-settings.maximum-lifetime", 1800000L);
        hikariConfig.setConnectionTimeout(connectionTimeoutMs);
        hikariConfig.setIdleTimeout(idleTimeoutMs);
        hikariConfig.setKeepaliveTime(keepaliveTimeMs);
        hikariConfig.setMaxLifetime(maxLifetimeMs);

        // set additional datasource properties
        for (final Map.Entry<String, Object> property : ADDITIONAL_PROPERTIES.entrySet()) {
            hikariConfig.addDataSourceProperty(property.getKey(), property.getValue());
        }

        // Add additional custom data source properties
        final ConfigurationSection propertiesSection = this.config.getConfigurationSection("connection-pool-settings.data-source-properties");
        if (propertiesSection != null) {
            final Set<String> properties = propertiesSection.getKeys(false);

            for (final String propertyName : properties) {
                final Object propertyValue = propertiesSection.get(propertyName);
                hikariConfig.addDataSourceProperty(propertyName, propertyValue);
            }
        }

        // initialize data source
        this.ds = new HikariDataSource(hikariConfig);

        // set table prefixer
        final String prefix = this.config.getString("table-prefix", "quests_");
        this.prefixer = s -> s.replace("{prefix}", prefix);

        // set whether quests ids should be validated
        this.validateQuests = this.plugin.getConfig().getBoolean("options.verify-quest-exists-on-load", true);

        // create and upgrade default tables
        try (final Connection conn = this.ds.getConnection()) {
            try (final Statement stmt = conn.createStatement()) {
                this.plugin.getQuestsLogger().debug("Creating default tables.");

                stmt.addBatch(this.prefixer.apply(CREATE_TABLE_QUEST_PROGRESS));
                stmt.addBatch(this.prefixer.apply(CREATE_TABLE_TASK_PROGRESS));
                stmt.addBatch(this.prefixer.apply(CREATE_TABLE_PLAYER_PREFERENCES));
                stmt.addBatch(this.prefixer.apply(CREATE_TABLE_DATABASE_INFORMATION));

                stmt.executeBatch();
            }

            final DatabaseMigrator migrator = new DatabaseMigrator(this.plugin, this.prefixer, conn);
            final int currentSchemaVersion = migrator.getCurrentSchemaVersion();

            // upgrade the table only if current schema version is lower than the latest
            if (currentSchemaVersion < DatabaseMigrator.LATEST_SCHEMA_VERSION) {
                this.plugin.getLogger().info("Automatically upgrading database schema from version " + currentSchemaVersion + " to " + DatabaseMigrator.LATEST_SCHEMA_VERSION + ".");
                migrator.upgrade(currentSchemaVersion);
            }
        } catch (final SQLException e) {
            throw new IOException("Failed to create or upgrade default tables", e);
        }

        this.fault = false;
    }

    @Override
    public void shutdown() {
        if (this.ds != null) {
            this.ds.close();
        }
    }

    @Override
    public @Nullable QPlayerData loadPlayerData(final @NotNull UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");

        if (this.fault) {
            return null;
        }

        final String uuidString = uuid.toString();
        final QuestProgressFile questProgressFile = new QuestProgressFile(this.plugin, uuid);

        try (final Connection conn = this.ds.getConnection()) {
            this.plugin.getQuestsLogger().debug("Querying player data for " + uuidString + ".");

            final Map<String, QuestProgress> questProgressMap = new HashMap<>();

            try (final PreparedStatement stmt = conn.prepareStatement(this.prefixer.apply(SELECT_PLAYER_QUEST_PROGRESS))) {
                stmt.setString(1, uuidString);

                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        final String questId = rs.getString(1);

                        if (this.validateQuests) {
                            final Quest quest = this.plugin.getQuestManager().getQuestById(questId);

                            if (quest == null) {
                                continue;
                            }
                        }

                        final boolean started = rs.getBoolean(2);
                        final long startedDate = rs.getLong(3);
                        final boolean completed = rs.getBoolean(4);
                        final boolean completedBefore = rs.getBoolean(5);
                        final long completionDate = rs.getLong(6);

                        final QuestProgress questProgress = new QuestProgress(this.plugin, questId, uuid, started, startedDate, completed, completedBefore, completionDate);
                        questProgressMap.put(questId, questProgress);
                    }
                }
            }

            try (final PreparedStatement stmt = conn.prepareStatement(this.prefixer.apply(SELECT_PLAYER_TASK_PROGRESS))) {
                stmt.setString(1, uuidString);

                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        final String questId = rs.getString(1);

                        final QuestProgress questProgress = questProgressMap.get(questId);
                        if (questProgress == null) {
                            continue;
                        }

                        final String taskId = rs.getString(2);

                        if (this.validateQuests) {
                            final Quest quest = this.plugin.getQuestManager().getQuestById(questId);
                            if (quest == null) {
                                continue;
                            }

                            final Task task = quest.getTaskById(taskId);
                            if (task == null) {
                                continue;
                            }
                        }

                        final boolean completed = rs.getBoolean(3);
                        final String progressString = rs.getString(4);
                        final String dataTypeString = rs.getString(5);

                        // maybe make an enum and use Enum#valueOf & then make a switch for enum instead?
                        // not sure about performance impact, probably just a small gain - need to benchmark it
                        final Object progress;
                        try {
                            progress = switch (dataTypeString) {
                                case null -> null;
                                case "int" -> Integer.parseInt(progressString);
                                case "float" -> Float.parseFloat(progressString);
                                case "long" -> Long.parseLong(progressString);
                                case "double" -> Double.parseDouble(progressString);
                                case "BigInteger" -> new BigInteger(progressString);
                                case "BigDecimal" -> new BigDecimal(progressString);
                                default -> throw new IllegalArgumentException("Unexpected data type: '" + dataTypeString + "'");
                            };
                        } catch (final NumberFormatException e) {
                            this.plugin.getLogger().log(Level.WARNING, "Cannot retrieve progress for task '" + taskId
                                    + "' in quest '" + questId + "' for player " + uuidString + " since progress string '"
                                    + progressString + "' is malformed!", e);
                            continue;
                        } catch (final IllegalArgumentException e) {
                            this.plugin.getLogger().log(Level.WARNING, "Cannot retrieve progress for task '" + taskId
                                    + "' in quest '" + questId + "' for player " + uuidString + " since data type string '"
                                    + dataTypeString + "' is unknown!", e);
                            continue;
                        }

                        final TaskProgress taskProgress = new TaskProgress(questProgress, taskId, uuid, progress, completed);
                        questProgress.addTaskProgress(taskProgress);
                    }
                }
            }

            final Collection<QuestProgress> allQuestProgress = questProgressMap.values();

            for (final QuestProgress questProgress : allQuestProgress) {
                questProgressFile.addQuestProgress(questProgress);
            }
        } catch (final SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to load player data for " + uuidString + ".", e);
            return null;
        }

        return new QPlayerData(uuid, new QPlayerPreferences(null), questProgressFile); // TODO player preferences
    }

    @Override
    public boolean savePlayerData(final @NotNull QPlayerData playerData) {
        Objects.requireNonNull(playerData, "playerData cannot be null");

        if (this.fault) {
            return false;
        }

        final UUID uuid = playerData.playerUUID();
        final String uuidString = uuid.toString(); // call it only once

        try (final Connection connection = this.ds.getConnection();
             final PreparedStatement questStmt = connection.prepareStatement(this.prefixer.apply(INSERT_PLAYER_QUEST_PROGRESS));
             final PreparedStatement taskStmt = connection.prepareStatement(this.prefixer.apply(INSERT_PLAYER_TASK_PROGRESS))) {

            this.plugin.getQuestsLogger().debug("Saving player data for " + uuidString + ".");

            final QuestProgressFile questProgressFile = playerData.questProgressFile();

            for (final QuestProgress questProgress : questProgressFile.getAllQuestProgress()) {
                if (!questProgress.isModified()) {
                    continue;
                }

                final String questId = questProgress.getQuestId();

                questStmt.setString(1, uuidString);
                questStmt.setString(2, questId);
                questStmt.setBoolean(3, questProgress.isStarted());
                questStmt.setLong(4, questProgress.getStartedDate());
                questStmt.setBoolean(5, questProgress.isCompleted());
                questStmt.setBoolean(6, questProgress.isCompletedBefore());
                questStmt.setLong(7, questProgress.getCompletionDate());
                questStmt.setBoolean(8, questProgress.isStarted());
                questStmt.setLong(9, questProgress.getStartedDate());
                questStmt.setBoolean(10, questProgress.isCompleted());
                questStmt.setBoolean(11, questProgress.isCompletedBefore());
                questStmt.setLong(12, questProgress.getCompletionDate());
                questStmt.addBatch();

                for (final TaskProgress taskProgress : questProgress.getTaskProgresses()) {
                    final String taskId = taskProgress.getTaskId();

                    final Object progress = taskProgress.getProgress();
                    final String progressString;
                    final String dataTypeString;

                    switch (progress) {
                        case null -> {
                            progressString = null;
                            dataTypeString = null;
                        }
                        case Integer i -> {
                            progressString = Integer.toString(i);
                            dataTypeString = "int";
                        }
                        case Float f -> {
                            progressString = Float.toString(f);
                            dataTypeString = "float";
                        }
                        case Long l -> {
                            progressString = Long.toString(l);
                            dataTypeString = "long";
                        }
                        case Double d -> {
                            progressString = Double.toString(d);
                            dataTypeString = "double";
                        }
                        case BigInteger bi -> {
                            progressString = bi.toString();
                            dataTypeString = "BigInteger";
                        }
                        case BigDecimal bd -> {
                            progressString = bd.toString();
                            dataTypeString = "BigDecimal";
                        }
                        default -> {
                            this.plugin.getLogger().warning("Cannot retrieve progress for task '" + taskId
                                    + "' in quest '" + questId + "' for player " + uuidString + " since a valid encoder for '"
                                    + progress.getClass().getName() + "' class has not been found!");
                            continue;
                        }
                    }

                    taskStmt.setString(1, uuidString);
                    taskStmt.setString(2, questId);
                    taskStmt.setString(3, taskId);
                    taskStmt.setBoolean(4, taskProgress.isCompleted());
                    taskStmt.setString(5, progressString);
                    taskStmt.setString(6, dataTypeString);
                    taskStmt.setBoolean(7, taskProgress.isCompleted());
                    taskStmt.setString(8, progressString);
                    taskStmt.setString(9, dataTypeString);
                    taskStmt.addBatch();
                }
            }

            questStmt.executeBatch();
            taskStmt.executeBatch();

            return true;
        } catch (final SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save player data for " + uuidString + ".", e);
            return false;
        }
    }

    @Override
    public @NotNull List<QPlayerData> loadAllPlayerData() {
        if (this.fault) {
            return Collections.emptyList();
        }

        final List<UUID> uuids = new ArrayList<>();

        try (final Connection conn = this.ds.getConnection();
             final PreparedStatement stmt = conn.prepareStatement(this.prefixer.apply(SELECT_UUID_LIST));
             final ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Get it by index to speed up it a little bit
                final String uuidString = rs.getString(1);

                final UUID uuid;
                try {
                    uuid = UUID.fromString(uuidString);
                } catch (final IllegalArgumentException e) {
                    this.plugin.getLogger().log(Level.SEVERE, "Failed to parse player UUID: '" + uuidString + "'.", e);
                    continue;
                }

                uuids.add(uuid);
            }
        } catch (final SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to load player UUIDs.", e);
            return Collections.emptyList();
        }

        final List<QPlayerData> allPlayerData = new ArrayList<>();

        for (final UUID uuid : uuids) {
            final QPlayerData playerData = this.loadPlayerData(uuid);

            if (playerData != null) {
                allPlayerData.add(playerData);
            }
        }

        return allPlayerData;
    }

    @SuppressWarnings("RedundantIfStatement") // I hate it, but keep it just for readability
    @Override
    public boolean isSimilar(final @NotNull StorageProvider otherProvider) {
        Objects.requireNonNull(otherProvider, "otherProvider cannot be null");

        if (!(otherProvider instanceof final ModernMySQLStorageProvider mySQLProvider)) {
            return false;
        }

        final String address = this.config.getString("network.address", "localhost:3306");
        final String otherAddress = mySQLProvider.config.getString("network.address", "localhost:3306");

        if (!address.equals(otherAddress)) {
            return false;
        }

        final String database = this.config.getString("network.database", "minecraft");
        final String otherDatabase = mySQLProvider.config.getString("network.database", "minecraft");

        if (!database.equals(otherDatabase)) {
            return false;
        }

        return true;
    }

    private record DatabaseMigrator(@NotNull BukkitQuestsPlugin plugin, @NotNull Function<String, String> prefixer, @NotNull Connection conn) {

        private static final String GET_STARTED_DATE_COLUMN =
                "SHOW COLUMNS from `{prefix}quest_progress` LIKE 'started_date';";
        private static final String SELECT_SCHEMA_VERSION =
                "SELECT value FROM `{prefix}database_information` WHERE `key` LIKE 'schema_version';";
        private static final String UPDATE_DATABASE_INFORMATION =
                "INSERT INTO `{prefix}database_information` (`key`, `value`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `value` = ?;";

        private static final int LATEST_SCHEMA_VERSION = 2;
        private static final Map<Integer, String> MIGRATION_STATEMENTS = new HashMap<>() {{
            this.put(1, "ALTER TABLE `{prefix}quest_progress` ADD COLUMN `started_date` BIGINT NOT NULL AFTER `started`;");
        }};

        private DatabaseMigrator(final @NotNull BukkitQuestsPlugin plugin, final @NotNull Function<String, String> prefixer, final @NotNull Connection conn) {
            this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
            this.prefixer = Objects.requireNonNull(prefixer, "prefixer cannot be null");
            this.conn = Objects.requireNonNull(conn, "conn cannot be null");
        }

        public int getInitialSchemaVersion() throws SQLException {
            this.plugin.getQuestsLogger().debug("Getting initial schema version for new database.");

            try (final Statement stmt = this.conn.createStatement();
                 final ResultSet rs = stmt.executeQuery(this.prefixer.apply(GET_STARTED_DATE_COLUMN))) {

                if (rs.next()) {
                    return LATEST_SCHEMA_VERSION;
                } else {
                    return 1;
                }
            }
        }

        public int getCurrentSchemaVersion() throws SQLException {
            this.plugin.getQuestsLogger().debug("Getting current schema version.");

            try (final Statement stmt = this.conn.createStatement();
                 final ResultSet rs = stmt.executeQuery(this.prefixer.apply(SELECT_SCHEMA_VERSION))) {

                if (rs.next()) {
                    final int version = Integer.parseUnsignedInt(rs.getString(1));
                    this.plugin.getQuestsLogger().debug("Current schema version: " + version + ".");
                    return version;
                }

                final int version = this.getInitialSchemaVersion();
                this.updateSchemaVersion(version);

                return version;
            }
        }

        public void updateSchemaVersion(final int updatedSchemaVersion) throws SQLException {
            this.plugin.getQuestsLogger().debug("Updating schema version to " + updatedSchemaVersion + ".");

            try (final PreparedStatement stmt = this.conn.prepareStatement(this.prefixer.apply(UPDATE_DATABASE_INFORMATION))) {
                stmt.setString(1, "schema_version");
                stmt.setString(2, Integer.toString(updatedSchemaVersion));
                stmt.setString(3, Integer.toString(updatedSchemaVersion));

                stmt.executeUpdate();
            }
        }

        public void upgrade(final int initialSchemaVersion) throws SQLException {
            this.plugin.getQuestsLogger().debug("Starting upgrade from version " + initialSchemaVersion + " to " + LATEST_SCHEMA_VERSION + ".");

            for (int i = initialSchemaVersion; i < LATEST_SCHEMA_VERSION; i++) {
                final String statementString = this.prefixer.apply(MIGRATION_STATEMENTS.get(i));
                this.plugin.getQuestsLogger().debug("Running migration statement: " + statementString + ".");

                try (final Statement stmt = this.conn.createStatement()) {
                    stmt.execute(statementString);
                } catch (final SQLException e) {
                    this.plugin.getLogger().severe("Failed to run migration statement (" + i + " -> " + (i + 1) + "): " + statementString + ".");
                    this.plugin.getLogger().severe("Quests will attempt to save current migration progress to prevent database corruption, but may not be able to do so.");
                    this.updateSchemaVersion(i);

                    // we still want it to throw and prevent further plugin loading
                    throw e;
                }
            }

            this.updateSchemaVersion(LATEST_SCHEMA_VERSION);
        }
    }
}
