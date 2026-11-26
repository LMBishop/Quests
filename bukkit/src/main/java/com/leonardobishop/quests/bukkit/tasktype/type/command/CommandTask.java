package com.leonardobishop.quests.bukkit.tasktype.type.command;

import com.leonardobishop.quests.bukkit.tasktype.BukkitTask;
import com.leonardobishop.quests.bukkit.tasktype.key.BukkitTaskKeys;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.tasktype.key.TaskKey;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParsers;
import com.leonardobishop.quests.common.tasktype.key.TaskOptions;
import com.leonardobishop.quests.common.tasktype.key.TaskSchema;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public final class CommandTask extends BukkitTask {

    public static final TaskKey<List<String>> COMMANDS = TaskKey.of("command", TaskKeyParsers.stringList()).withAliases("commands").required();
    public static final TaskKey<TaskUtils.StringMatchMode> COMMAND_MATCH_MODE = BukkitTaskKeys.matchMode("command-match-mode");
    public static final TaskKey<Boolean> IGNORE_CASE = TaskKey.of("ignore-case", TaskKeyParsers.booleanValue()).withDefault(false);

    public static final TaskSchema<CommandTask> SCHEMA = BukkitTask.schema(CommandTask::new)
            .keys(COMMANDS, COMMAND_MATCH_MODE, IGNORE_CASE)
            .build();

    public final List<String> commands;
    public final TaskUtils.StringMatchMode commandMatchMode;
    public final boolean ignoreCase;

    public CommandTask(final String id, final String type, final TaskOptions options) {
        super(id, type, options);

        this.commands = options.get(COMMANDS);
        this.commandMatchMode = options.get(COMMAND_MATCH_MODE);
        this.ignoreCase = options.get(IGNORE_CASE);
    }
}
