package com.leonardobishop.quests.bukkit.storage;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.storage.StorageProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

public class MySqlStorageProvider implements StorageProvider {

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
    private static final String CREATE_TABLE_DATABASE_INFORMATION =
            "CREATE TABLE IF NOT EXISTS `{prefix}database_information` (" +
                    " `key`   VARCHAR(255) NOT NULL," +
                    " `value` VARCHAR(255) NOT NULL," +
                    " PRIMARY KEY (`key`));";
    private static final String SELECT_PLAYER_QUEST_PROGRESS =
            "SELECT quest_id, started, started_date, completed, completed_before, completion_date FROM `{prefix}quest_progress` WHERE uuid=?;";
    private static final String SELECT_PLAYER_TASK_PROGRESS =
            "SELECT quest_id, task_id, completed, progress, data_type FROM `{prefix}task_progress` WHERE uuid=?;";
    private static final String SELECT_UUID_LIST =
            "SELECT DISTINCT uuid FROM `{prefix}quest_progress`;";
    private static final String SELECT_KNOWN_PLAYER_QUEST_PROGRESS =
            "SELECT quest_id FROM `{prefix}quest_progress` WHERE uuid=?;";
    private static final String SELECT_KNOWN_PLAYER_TASK_PROGRESS =
            "SELECT quest_id, task_id FROM `{prefix}task_progress` WHERE uuid=?;";
    private static final String WRITE_PLAYER_QUEST_PROGRESS =
            "INSERT INTO `{prefix}quest_progress` (uuid, quest_id, started, started_date, completed, completed_before, completion_date) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE started=?, started_date=?, completed=?, completed_before=?, completion_date=?";
    private static final String WRITE_PLAYER_TASK_PROGRESS =
            "INSERT INTO `{prefix}task_progress` (uuid, quest_id, task_id, completed, progress, data_type) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE completed=?, progress=?, data_type=?";

    private final ConfigurationSection configuration;
    private final BukkitQuestsPlugin plugin;
    private HikariDataSource hikari;
    private String prefix;
    private Function<String, String> statementProcessor;
    private boolean fault;

    public MySqlStorageProvider(BukkitQuestsPlugin plugin, ConfigurationSection configuration) {
        this.plugin = plugin;
        if (configuration == null) {
            configuration = new YamlConfiguration();
        }
        this.configuration = configuration;
        this.fault = true;
    }

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public void init() {
        String address = configuration.getString("network.address", "localhost:3306");
        String database = configuration.getString("network.database", "minecraft");
        String url = "jdbc:mysql://" + address + "/" + database;

        HikariConfig config = new HikariConfig();
        config.setPoolName("quests-hikari");

        config.setUsername(configuration.getString("network.username", "root"));
        config.setPassword(configuration.getString("network.password", ""));
        config.setJdbcUrl(url);
        config.setMaximumPoolSize(configuration.getInt("connection-pool-settings.maximum-pool-size", 8));
        config.setMinimumIdle(configuration.getInt("connection-pool-settings.minimum-idle", 8));
        config.setMaxLifetime(configuration.getInt("connection-pool-settings.maximum-lifetime", 1800000));
        config.setConnectionTimeout(configuration.getInt("connection-pool-settings.connection-timeout", 5000));

        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);

        if (configuration.isConfigurationSection("connection-pool-settings.data-source-properties")) {
            for (String property : configuration.getConfigurationSection("connection-pool-settings.data-source-properties").getKeys(false)) {
                config.addDataSourceProperty(property, configuration.get("connection-pool-settings.data-source-properties." + property));
            }
        }

        this.hikari = new HikariDataSource(config);
        this.prefix = configuration.getString("database-settings.table-prefix", "quests_");
        this.statementProcessor = s -> s.replace("{prefix}", prefix);
        try (Connection connection = hikari.getConnection()) {
            try (Statement s = connection.createStatement()) {
                plugin.getQuestsLogger().debug("Creating default tables");
                s.addBatch(this.statementProcessor.apply(CREATE_TABLE_QUEST_PROGRESS));
                s.addBatch(this.statementProcessor.apply(CREATE_TABLE_TASK_PROGRESS));
                s.addBatch(this.statementProcessor.apply(CREATE_TABLE_DATABASE_INFORMATION));

                s.executeBatch();
            }
            DatabaseMigrator migrator = new DatabaseMigrator(connection);

            int currentVersion = migrator.getCurrentSchemaVersion();
            if (currentVersion < DatabaseMigrator.CURRENT_SCHEMA_VERSION) {
                plugin.getQuestsLogger().info("Automatically upgrading database schema from version " + currentVersion + " to " + DatabaseMigrator.CURRENT_SCHEMA_VERSION);
                migrator.upgrade(currentVersion);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.fault = false;
    }

    @Override
    public void shutdown() {
        if (hikari != null) hikari.close();
    }

    @Override
    @Nullable
    public QuestProgressFile loadProgressFile(@NotNull UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");

        if (fault) return null;
        Map<String, Quest> presentQuests = new HashMap<>(plugin.getQuestManager().getQuests());
        boolean validateQuests = plugin.getQuestsConfig().getBoolean("options.verify-quest-exists-on-load", true);

        QuestProgressFile questProgressFile = new QuestProgressFile(uuid, plugin);
        try (Connection connection = hikari.getConnection()) {
            plugin.getQuestsLogger().debug("Querying player " + uuid);
            Map<String, QuestProgress> questProgressMap = new HashMap<>();
            try (PreparedStatement ps = connection.prepareStatement(this.statementProcessor.apply(SELECT_PLAYER_QUEST_PROGRESS))) {
                ps.setString(1, uuid.toString());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String questId = rs.getString(1);
                        boolean started = rs.getBoolean(2);
                        long startedDate = rs.getLong(3);
                        boolean completed = rs.getBoolean(4);
                        boolean completedBefore = rs.getBoolean(5);
                        long completionDate = rs.getLong(6);

                        if (validateQuests && !presentQuests.containsKey(questId)) continue;
                        QuestProgress questProgress = new QuestProgress(plugin, questId, completed, completedBefore, completionDate, uuid, started, startedDate);
                        questProgressMap.put(questId, questProgress);
                    }
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(this.statementProcessor.apply(SELECT_PLAYER_TASK_PROGRESS))) {
                ps.setString(1, uuid.toString());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String questId = rs.getString(1);
                        String taskId = rs.getString(2);
                        boolean completed = rs.getBoolean(3);
                        String encodedProgress = rs.getString(4);
                        String type = rs.getString(5);
                        Object progress;
                        try {
                            if (type == null) {
                                progress = null;
                            } else if (type.equals("double")) {
                                progress = Double.valueOf(encodedProgress);
                            } else if (type.equals("float")) {
                                progress = Float.valueOf(encodedProgress);
                            } else if (type.equals("int")) {
                                progress = Integer.valueOf(encodedProgress);
                            } else if (type.equals("BigDecimal")) {
                                progress = new BigDecimal(encodedProgress);
                            } else {
                                throw new RuntimeException("unknown data type '" + type + "'");
                            }
                        } catch (NumberFormatException ex) {
                            plugin.getQuestsLogger().warning("Cannot retrieve progress for task '"
                                    + taskId + "' in quest '" + questId + "' for player " + uuid
                                    + " since data is malformed!");
                            continue;
                        } catch (RuntimeException ex) {
                            if (ex.getMessage().startsWith("unknown data type ")) {
                                plugin.getQuestsLogger().warning("Cannot retrieve progress for task '"
                                        + taskId + "' in quest '" + questId + "' for player " + uuid
                                        + ": " + ex.getMessage());
                                continue;
                            } else {
                                throw ex;
                            }
                        }

                        QuestProgress linkedQuestProgress = questProgressMap.get(questId);
                        if (linkedQuestProgress == null) continue;
                        if (validateQuests) {
                            if (!presentQuests.containsKey(questId)) continue;
                            if (presentQuests.get(questId).getTaskById(taskId) == null) continue;
                        }
                        TaskProgress questProgress = new TaskProgress(linkedQuestProgress, taskId, progress, uuid, completed);
                        linkedQuestProgress.addTaskProgress(questProgress);
                    }
                }
            }
            for (QuestProgress questProgress : questProgressMap.values()) {
                questProgressFile.addQuestProgress(questProgress);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return questProgressFile;
    }

    @Override
    public boolean saveProgressFile(@NotNull UUID uuid, @NotNull QuestProgressFile questProgressFile) {
        Objects.requireNonNull(uuid, "uuid cannot be null");
        Objects.requireNonNull(questProgressFile, "questProgressFile cannot be null");

        if (fault) return false;
        try (Connection connection = hikari.getConnection()) {
            try (PreparedStatement writeQuestProgress = connection.prepareStatement(this.statementProcessor.apply(WRITE_PLAYER_QUEST_PROGRESS));
                 PreparedStatement writeTaskProgress = connection.prepareStatement(this.statementProcessor.apply(WRITE_PLAYER_TASK_PROGRESS))) {

                List<QuestProgress> questProgressValues = new ArrayList<>(questProgressFile.getAllQuestProgress());
                for (QuestProgress questProgress : questProgressValues) {
                    if (!questProgress.isModified()) continue;
                    
                    String questId = questProgress.getQuestId();
                    writeQuestProgress.setString(1, uuid.toString());
                    writeQuestProgress.setString(2, questProgress.getQuestId());
                    writeQuestProgress.setBoolean(3, questProgress.isStarted());
                    writeQuestProgress.setLong(4, questProgress.getStartedDate());
                    writeQuestProgress.setBoolean(5, questProgress.isCompleted());
                    writeQuestProgress.setBoolean(6, questProgress.isCompletedBefore());
                    writeQuestProgress.setLong(7, questProgress.getCompletionDate());
                    writeQuestProgress.setBoolean(8, questProgress.isStarted());
                    writeQuestProgress.setLong(9, questProgress.getStartedDate());
                    writeQuestProgress.setBoolean(10, questProgress.isCompleted());
                    writeQuestProgress.setBoolean(11, questProgress.isCompletedBefore());
                    writeQuestProgress.setLong(12, questProgress.getCompletionDate());
                    writeQuestProgress.addBatch();

                    for (TaskProgress taskProgress : questProgress.getTaskProgress()) {
                        String taskId = taskProgress.getTaskId();

                        String encodedProgress;
                        Object progress = taskProgress.getProgress();
                        String type;
                        if (progress == null) {
                            type = null;
                            encodedProgress = null;
                        } else if (progress instanceof Double) {
                            type = "double";
                            encodedProgress = String.valueOf(progress);
                        } else if (progress instanceof Integer) {
                            type = "int";
                            encodedProgress = String.valueOf(progress);
                        } else if (progress instanceof Float) {
                            type = "float";
                            encodedProgress = String.valueOf(progress);
                        } else if (progress instanceof BigDecimal) {
                            type = "BigDecimal";
                            encodedProgress = String.valueOf(progress);
                        } else {
                            plugin.getQuestsLogger().warning("Cannot store progress for task '"
                                    + taskId + "' in quest '" + questId + "' for player " + uuid
                                    + " since type " + progress.getClass().getName() + " cannot be encoded!");
                            continue;
                        }
                        writeTaskProgress.setString(1, uuid.toString());
                        writeTaskProgress.setString(2, questId);
                        writeTaskProgress.setString(3, taskProgress.getTaskId());
                        writeTaskProgress.setBoolean(4, taskProgress.isCompleted());
                        writeTaskProgress.setString(5, encodedProgress);
                        writeTaskProgress.setString(6, type);
                        writeTaskProgress.setBoolean(7, taskProgress.isCompleted());
                        writeTaskProgress.setString(8, encodedProgress);
                        writeTaskProgress.setString(9, type);
                        writeTaskProgress.addBatch();
                    }
                }

                writeQuestProgress.executeBatch();
                writeTaskProgress.executeBatch();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public @NotNull List<QuestProgressFile> loadAllProgressFiles() {
        if (fault) return Collections.emptyList();

        Set<UUID> uuids = new HashSet<>();

        try (Connection connection = hikari.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(this.statementProcessor.apply(SELECT_UUID_LIST))) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String uuidString = rs.getString(1);
                        try {
                            UUID uuid = UUID.fromString(uuidString);
                            uuids.add(uuid);
                        } catch (IllegalArgumentException ignored) { }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        List<QuestProgressFile> files = new ArrayList<>();
        for (UUID uuid : uuids) {
            QuestProgressFile file = loadProgressFile(uuid);
            if (file != null) {
                files.add(file);
            }
        }

        return files;
    }

    @Override
    public void saveAllProgressFiles(List<QuestProgressFile> files) {
        if (fault) return;

        for (QuestProgressFile file : files) {
            saveProgressFile(file.getPlayerUUID(), file);
        }
    }

    @Override
    public boolean isSimilar(StorageProvider provider) {
        if (!(provider instanceof MySqlStorageProvider)) {
            return false;
        }

        MySqlStorageProvider other = (MySqlStorageProvider) provider;

        String address = configuration.getString("network.address", "localhost:3306");
        String database = configuration.getString("network.database", "minecraft");

        String otherAddress = other.configuration.getString("network.address", "localhost:3306");
        String otherDatabase = other.configuration.getString("network.database", "minecraft");

        return address.equalsIgnoreCase(otherAddress) && database.equalsIgnoreCase(otherDatabase);
    }

    private class DatabaseMigrator {
        private static final String GET_STARTED_DATE_COLUMN =
                "SHOW COLUMNS from `{prefix}quest_progress` LIKE 'started_date';";
        private static final String SELECT_SCHEMA_VERSION =
                "SELECT value FROM `{prefix}database_information` WHERE `key`='schema_version';";
        private static final String UPDATE_DATABASE_INFORMATION =
                "INSERT INTO `{prefix}database_information` (`key`, `value`) VALUES (?,?) ON DUPLICATE KEY UPDATE `value`=?;";
        private static final int CURRENT_SCHEMA_VERSION = 2;

        private final Map<Integer, String> migrationStatements = new HashMap<>();

        private final Connection connection;

        public DatabaseMigrator(Connection connection) {
            this.connection = connection;

            this.migrationStatements.put(1,
                    "ALTER TABLE `{prefix}quest_progress` ADD COLUMN `started_date` BIGINT NOT NULL AFTER `started`;");
        }

        public int getInitialSchemaVersion() {
            try (Statement statement = connection.createStatement()) {
                plugin.getQuestsLogger().debug("Getting initial schema version for new database");
                ResultSet rs = statement.executeQuery(statementProcessor.apply(GET_STARTED_DATE_COLUMN));
                boolean hasStartedDateColumn = rs.next();

                return hasStartedDateColumn ? CURRENT_SCHEMA_VERSION : 1;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public int getCurrentSchemaVersion() {
            try (Statement statement = connection.createStatement()) {
                plugin.getQuestsLogger().debug("Getting current schema version");
                ResultSet rs = statement.executeQuery(statementProcessor.apply(SELECT_SCHEMA_VERSION));
                if (rs.next()) {
                    int version = Integer.parseInt(rs.getString(1));
                    plugin.getQuestsLogger().debug("Current schema version: " + version);
                    return version;
                } else {
                    int initialVersion = getInitialSchemaVersion();
                    updateSchemaVersion(initialVersion);
                    return initialVersion;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void upgrade(int initialSchemaVersion) {
            plugin.getQuestsLogger().debug("Starting upgrade from version " + initialSchemaVersion + " to " + CURRENT_SCHEMA_VERSION);
            for (int i = initialSchemaVersion; i < CURRENT_SCHEMA_VERSION; i++) {
                String statement = statementProcessor.apply(migrationStatements.get(i));
                plugin.getQuestsLogger().debug("Running migration statement: " + statement);
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(statementProcessor.apply(statement));
                } catch (SQLException e) {
                    plugin.getQuestsLogger().severe("Failed to run migration statement (" + i + " -> " + (i + 1) + "): " + statement);
                    plugin.getQuestsLogger().severe("Quests will attempt to save current migration progress to prevent database corruption, but may not be able to do so");
                    updateSchemaVersion(i);
                    throw new RuntimeException(e);
                }
            }
            updateSchemaVersion(CURRENT_SCHEMA_VERSION);
        }

        public void updateSchemaVersion(int version) {
            plugin.getQuestsLogger().debug("Updating schema version to " + version);
            try (PreparedStatement stmt = connection.prepareStatement(statementProcessor.apply(UPDATE_DATABASE_INFORMATION))) {
                stmt.setString(1, "schema_version");
                stmt.setString(2, String.valueOf(version));
                stmt.setString(3, String.valueOf(version));

                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
