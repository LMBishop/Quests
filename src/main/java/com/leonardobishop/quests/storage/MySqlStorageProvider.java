package com.leonardobishop.quests.storage;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class MySqlStorageProvider implements StorageProvider {

    private static final String CREATE_TABLE_QUEST_PROGRESS =
            "CREATE TABLE IF NOT EXISTS `{prefix}quest_progress` (" +
                    " `id`                INT          NOT NULL AUTO_INCREMENT," +
                    " `uuid`              VARCHAR(36)  NOT NULL," +
                    " `quest_id`          VARCHAR(50)  NOT NULL," +
                    " `started`           BOOL         NOT NULL," +
                    " `completed`         BOOL         NOT NULL," +
                    " `completed_before`  BOOL         NOT NULL," +
                    " `completion_date`   BIGINT       NOT NULL," +
                    " PRIMARY KEY (`id`));";
    private static final String CREATE_TABLE_TASK_PROGRESS =
            "CREATE TABLE IF NOT EXISTS `{prefix}task_progress` (" +
                    " `id`         INT          NOT NULL AUTO_INCREMENT," +
                    " `uuid`       VARCHAR(36)  NOT NULL," +
                    " `quest_id`   VARCHAR(50)  NOT NULL," +
                    " `task_id`    VARCHAR(50)  NOT NULL," +
                    " `completed`  BOOL         NOT NULL," +
                    " `progress`   VARCHAR(64)  NULL," +
                    " `data_type`  VARCHAR(10)  NULL," +
                    " PRIMARY KEY (`id`));";
    private static final String SELECT_PLAYER_QUEST_PROGRESS =
            "SELECT quest_id, started, completed, completed_before, completion_date FROM `{prefix}quest_progress` WHERE uuid=?;";
    private static final String SELECT_PLAYER_TASK_PROGRESS =
            "SELECT quest_id, task_id, completed, progress, data_type FROM `{prefix}task_progress` WHERE uuid=?;";
    private static final String SELECT_KNOWN_PLAYER_QUEST_PROGRESS =
            "SELECT quest_id FROM `{prefix}quest_progress` WHERE uuid=?;";
    private static final String SELECT_KNOWN_PLAYER_TASK_PROGRESS =
            "SELECT quest_id, task_id FROM `{prefix}task_progress` WHERE uuid=?;";
    private static final String INSERT_PLAYER_QUEST_PROGRESS =
            "INSERT INTO `{prefix}quest_progress` (uuid, quest_id, started, completed, completed_before, completion_date) VALUES (?,?,?,?,?,?)";
    private static final String INSERT_PLAYER_TASK_PROGRESS =
            "INSERT INTO `{prefix}task_progress` (uuid, quest_id, task_id, completed, progress, data_type) VALUES (?,?,?,?,?,?)";
    private static final String UPDATE_PLAYER_QUEST_PROGRESS =
            "UPDATE `{prefix}quest_progress` SET started=?, completed=?, completed_before=?, completion_date=? WHERE uuid=? AND quest_id=?";
    private static final String UPDATE_PLAYER_TASK_PROGRESS =
            "UPDATE `{prefix}task_progress` SET completed=?, progress=?, data_type=? WHERE uuid=? AND quest_id=? AND task_id=?";

    private final HikariDataSource hikari;
    private final String prefix;
    private final Quests plugin;
    private final Function<String, String> statementProcessor;

    public MySqlStorageProvider(Quests plugin, ConfigurationSection configuration) {
        this.plugin = plugin;
        if (configuration == null) {
            configuration = new YamlConfiguration();
        }

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

        this.hikari = new HikariDataSource(config);
        this.prefix = configuration.getString("database-settings.table-prefix", "quests_");
        this.statementProcessor = s -> s.replace("{prefix}", prefix);
    }

    @Override
    public void init() {
        try (Connection connection = hikari.getConnection()) {
            try (Statement s = connection.createStatement()) {
                plugin.getQuestsLogger().debug("Creating default tables");
                s.addBatch(this.statementProcessor.apply(CREATE_TABLE_QUEST_PROGRESS));
                s.addBatch(this.statementProcessor.apply(CREATE_TABLE_TASK_PROGRESS));

                s.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        if (hikari != null) hikari.close();
    }

    @Override
    public QuestProgressFile loadProgressFile(UUID uuid) {
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
                        boolean completed = rs.getBoolean(3);
                        boolean completedBefore = rs.getBoolean(4);
                        long completionDate = rs.getLong(5);

                        QuestProgress questProgress = new QuestProgress(plugin, questId, completed, completedBefore, completionDate, uuid, started);
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
                        if (linkedQuestProgress == null) continue; // lost quest progress ?
                        TaskProgress questProgress = new TaskProgress(linkedQuestProgress, taskId, progress, uuid, completed);
                        linkedQuestProgress.addTaskProgress(questProgress);
                    }
                }
            }
            for (QuestProgress questProgress : questProgressMap.values()) {
                questProgressFile.addQuestProgress(questProgress);
            }
        } catch (SQLException e) {
            plugin.getQuestsLogger().severe("Failed to load player: " + uuid + "!");
            e.printStackTrace();
        }
        return questProgressFile;
    }

    @Override
    public void saveProgressFile(UUID uuid, QuestProgressFile questProgressFile) {
        try (Connection connection = hikari.getConnection()) {
            plugin.getQuestsLogger().debug("Getting known entries for player " + uuid);
            List<String> knownQuestIds = new ArrayList<>();
            Map<String, List<String>> knownTaskIds = new HashMap<>();
            try (PreparedStatement ps = connection.prepareStatement(this.statementProcessor.apply(SELECT_KNOWN_PLAYER_QUEST_PROGRESS))) {
                ps.setString(1, uuid.toString());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        knownQuestIds.add(rs.getString(1));
                    }
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(this.statementProcessor.apply(SELECT_KNOWN_PLAYER_TASK_PROGRESS))) {
                ps.setString(1, uuid.toString());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String questId = rs.getString(1);
                        String taskId = rs.getString(2);

                        knownTaskIds.putIfAbsent(questId, new ArrayList<>());
                        knownTaskIds.get(questId).add(taskId);
                    }
                }
            }

            try (PreparedStatement insertQuestProgress = connection.prepareStatement(this.statementProcessor.apply(INSERT_PLAYER_QUEST_PROGRESS));
                 PreparedStatement insertTaskProgress = connection.prepareStatement(this.statementProcessor.apply(INSERT_PLAYER_TASK_PROGRESS));
                 PreparedStatement updateQuestProgress = connection.prepareStatement(this.statementProcessor.apply(UPDATE_PLAYER_QUEST_PROGRESS));
                 PreparedStatement updateTaskProgress = connection.prepareStatement(this.statementProcessor.apply(UPDATE_PLAYER_TASK_PROGRESS))) {

                List<QuestProgress> questProgressValues = new ArrayList<>(questProgressFile.getAllQuestProgress());
                for (QuestProgress questProgress : questProgressValues) {
                    if (!questProgress.isModified()) continue;
                    
                    String questId = questProgress.getQuestId();
                    if (knownQuestIds.contains(questId)) {
                        updateQuestProgress.setBoolean(1, questProgress.isStarted());
                        updateQuestProgress.setBoolean(2, questProgress.isCompleted());
                        updateQuestProgress.setBoolean(3, questProgress.isCompletedBefore());
                        updateQuestProgress.setLong(4, questProgress.getCompletionDate());
                        updateQuestProgress.setString(5, uuid.toString());
                        updateQuestProgress.setString(6, questId);
                        updateQuestProgress.addBatch();
                    } else {
                        insertQuestProgress.setString(1, uuid.toString());
                        insertQuestProgress.setString(2, questProgress.getQuestId());
                        insertQuestProgress.setBoolean(3, questProgress.isStarted());
                        insertQuestProgress.setBoolean(4, questProgress.isCompleted());
                        insertQuestProgress.setBoolean(5, questProgress.isCompletedBefore());
                        insertQuestProgress.setLong(6, questProgress.getCompletionDate());
                        insertQuestProgress.addBatch();
                    }
                    List<String> taskIds = knownTaskIds.getOrDefault(questProgress.getQuestId(), Collections.emptyList());
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
                        } else {
                            plugin.getQuestsLogger().warning("Cannot store progress for task '"
                                    + taskId + "' in quest '" + questId + "' for player " + uuid
                                    + " since type " + progress.getClass().getName() + " cannot be encoded!");
                            continue;
                        }
                        if (taskIds.contains(taskId)) {
                            updateTaskProgress.setBoolean(1, taskProgress.isCompleted());
                            updateTaskProgress.setString(2, encodedProgress);
                            updateTaskProgress.setString(3, type);
                            updateTaskProgress.setString(4, uuid.toString());
                            updateTaskProgress.setString(5, questId);
                            updateTaskProgress.setString(6, taskId);
                            updateTaskProgress.addBatch();
                        } else {
                            insertTaskProgress.setString(1, uuid.toString());
                            insertTaskProgress.setString(2, questId);
                            insertTaskProgress.setString(3, taskProgress.getTaskId());
                            insertTaskProgress.setBoolean(4, taskProgress.isCompleted());
                            insertTaskProgress.setString(5, encodedProgress);
                            insertTaskProgress.setString(6, type);
                            insertTaskProgress.addBatch();
                        }
                    }
                }

                insertQuestProgress.executeBatch();
                insertTaskProgress.executeBatch();
                updateQuestProgress.executeBatch();
                updateTaskProgress.executeBatch();
            }
        } catch (SQLException e) {
            plugin.getQuestsLogger().severe("Failed to save player: " + uuid + "!");
            e.printStackTrace();
        }
    }
}
