package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import org.bukkit.command.CommandSender;

//TODO refactor this
public enum Messages {

    // Title
    TITLE_QUEST_START_TITLE("titles.quest-start.title"),
    TITLE_QUEST_START_SUBTITLE("titles.quest-start.subtitle"),
    TITLE_QUEST_COMPLETE_TITLE("titles.quest-complete.title"),
    TITLE_QUEST_COMPLETE_SUBTITLE("titles.quest-complete.subtitle"),

    // Chat messages
    TIME_FORMAT("messages.time-format", "{hours}h {minutes}m"),
    QUEST_START("messages.quest-start", "&7Quest &c{quest} &7started!"),
    QUEST_COMPLETE("messages.quest-complete", "&7Quest &c{quest} &completed!"),
    QUEST_CANCEL("messages.quest-cancel", "&7Quest &c{quest} &7cancelled!"),
    QUEST_EXPIRE("messages.quest-expire", "&7Quest &c{quest} &7has expired."),
    QUEST_TRACK("messages.quest-track", "&7Tracking quest &c{quest}&7."),
    QUEST_TRACK_STOP("messages.quest-track-stop", "&7No longer tracking quest &c{quest}&7."),
    QUEST_RANDOM_NONE("messages.quest-random-none", "&cYou have no quests which you can start."),
    QUEST_START_LIMIT("messages.quest-start-limit", "&7Players are limited to &c{limit} &7started quests at a time."),
    QUEST_START_DISABLED("messages.quest-start-disabled", "&7You cannot repeat this quest."),
    QUEST_START_LOCKED("messages.quest-start-locked", "&7You have not unlocked this quest yet."),
    QUEST_START_COOLDOWN("messages.quest-start-cooldown", "&7You have recently completed this quest. You have to wait &c{time} &7until you are able to restart it."),
    QUEST_START_STARTED("messages.quest-start-started", "&7You have already started this quest."),
    QUEST_START_PERMISSION("messages.quest-start-permission", "&7You do not have permission to start this quest."),
    QUEST_CATEGORY_QUEST_PERMISSION("messages.quest-category-quest-permission", "&7You do not have permission to view this category."),
    QUEST_CATEGORY_PERMISSION("messages.quest-category-permission", "&7You do not have permission to start this quest since it is in a category you do not have permission to view."),
    QUEST_CANCEL_NOTSTARTED("messages.quest-cancel-notstarted", "&7You have not started this quest."),
    QUEST_CANCEL_NOTCANCELLABLE("messages.quest-cancel-notcancellable", "&7You cannot cancel this quest."),
    QUEST_UPDATER("messages.quest-updater", "&cQuests > &7A new version &c{newver} &7was found on Spigot (your version: &c{oldver}&7). Please update me! <3 - Link: {link}"),
    COMMAND_DATA_NOT_LOADED("messages.command-data-not-loaded", "&4Your quests progress file has not been loaded; you cannot use quests. If this issue persists, contact an admin."),
    COMMAND_SUB_DOESNTEXIST("messages.command-sub-doesntexist", "&7The specified subcommand '&c{sub}' &7does not exist."),
    COMMAND_NO_PERMISSION("messages.command-no-permission", "&7You do not have permission to use this command."),
    COMMAND_QUEST_CANCEL_SPECIFY("messages.quest-cancel-specify", "&7You must specify a quest to cancel."),
    COMMAND_QUEST_START_DOESNTEXIST("messages.command-quest-start-doesntexist", "&7The specified quest '&c{quest}&7' does not exist."),
    COMMAND_QUEST_GENERAL_DOESNTEXIST("messages.command-quest-general-doesntexist", "&7The specified quest '&c{quest}&7' does not exist."),
    COMMAND_QUEST_OPENCATEGORY_ADMIN_SUCCESS("messages.command-quest-opencategory-admin-success", "&7Opened category &c{category} &7for player &c{player}&7."),
    COMMAND_QUEST_OPENQUESTS_ADMIN_SUCCESS("messages.command-quest-openquests-admin-success", "&7Opened Quest GUI for player &c{player}&7."),
    COMMAND_QUEST_OPENSTARTED_ADMIN_SUCCESS("messages.command-quest-openstarted-admin-success", "&7Opened started Quest GUI for player &c{player}&7."),
    COMMAND_QUEST_ADMIN_PLAYERNOTFOUND("messages.command-quest-admin-playernotfound", "&7Player '&c{player}&7' could not be found."),
    COMMAND_CATEGORY_OPEN_DOESNTEXIST("messages.command-category-open-doesntexist", "&7The specified category '&c{category}&7' does not exist."),
    COMMAND_CATEGORY_OPEN_DISABLED("messages.command-category-open-disabled", "&7Categories are disabled."),
    COMMAND_TASKVIEW_ADMIN_FAIL("messages.command-taskview-admin-fail", "&7Task type '&c{task}&7' does not exist."),
    BETA_REMINDER("messages.beta-reminder", "&cQuests > &7Reminder: you are currently using a &cbeta &7version of Quests. Please send bug reports to https://github.com/LMBishop/Quests/issues and check for updates regularly using &c/quests admin update&7!"),
    COMMAND_QUEST_ADMIN_LOADDATA("messages.command-quest-admin-loaddata", "&7Quest data for '&c{player}&7' is being loaded."),
    COMMAND_QUEST_ADMIN_NODATA("messages.command-quest-admin-nodata", "&7No data could be found for player &c{player}&7."),
    COMMAND_QUEST_ADMIN_FULLRESET("messages.command-quest-admin-fullreset", "&7Data for player &c{player}&7 has been fully reset."),
    COMMAND_QUEST_ADMIN_START_FAILLOCKED("messages.command-quest-admin-start-faillocked", "&7Quest '&c{quest}&7' could not be started for player &c{player}&7. They have not yet unlocked it."),
    COMMAND_QUEST_ADMIN_START_FAILCOOLDOWN("messages.command-quest-admin-start-failcooldown", "&7Quest '&c{quest}&7' could not be started for player &c{player}&7. It is still on cooldown for them."),
    COMMAND_QUEST_ADMIN_START_FAILCOMPLETE("messages.command-quest-admin-start-failcomplete", "&7Quest '&c{quest}&7' could not be started for player &c{player}&7. They have already completed it."),
    COMMAND_QUEST_ADMIN_START_FAILLIMIT("messages.command-quest-admin-start-faillimit", "&7Quest '&c{quest}&7' could not be started for player &c{player}&7. They have reached their quest start limit."),
    COMMAND_QUEST_ADMIN_START_FAILSTARTED("messages.command-quest-admin-start-failstarted", "&7Quest '&c{quest}&7' could not be started for player &c{player}&7. It is already started."),
    COMMAND_QUEST_ADMIN_START_FAILPERMISSION("messages.command-quest-admin-start-failpermission", "&7Quest '&c{quest}&7' could not be started for player &c{player}&7. They do not have permission."),
    COMMAND_QUEST_ADMIN_START_FAILCATEGORYPERMISSION("messages.command-quest-admin-start-failcategorypermission", "&7Quest '&c{quest}&7' could not be started for player &c{player}&7. They do not have permission for the category which the quest is in."),
    COMMAND_QUEST_ADMIN_START_FAILOTHER("messages.command-quest-admin-start-failother", "&7Quest '&c{quest}&7' could not be started for player &c{player}&7."),
    COMMAND_QUEST_ADMIN_START_SUCCESS("messages.command-quest-admin-start-success", "&7Quest &c{quest} &7started for player &c{player}&7."),
    COMMAND_QUEST_ADMIN_CATEGORY_PERMISSION("messages.command-quest-admin-category-permission", "&7Category &c{category} &7 could not be opened for player &c{player}&7. They do not have permission to view it."),
    COMMAND_QUEST_ADMIN_COMPLETE_SUCCESS("messages.command-quest-admin-complete-success", "&7Quest &c{quest} &7completed for player &c{player}&7."),
    COMMAND_QUEST_ADMIN_RESET_SUCCESS("messages.command-quest-admin-reset-success", "&7Successfully reset quest '&c{quest}&7' for player &c{player}&7."),
    COMMAND_QUEST_ADMIN_RANDOM_NONE("messages.command-quest-admin-random-none", "&7Player &c{player}&7 has no quests which they can start."),
    COMMAND_QUEST_ADMIN_RANDOM_SUCCESS("messages.command-quest-admin-random-success", "&7Successfully started random quest '&c{quest}&7' for player &c{player}&7."),
    COMMAND_QUEST_ADMIN_RANDOM_CATEGORY_NONE("messages.command-quest-admin-random-category-none", "&7Player &c{player}&7 has no quests in category '&c{category}&7' which they can start."),
    COMMAND_QUEST_ADMIN_RANDOM_CATEGORY_SUCCESS("messages.command-quest-admin-random-category-success", "&7Successfully started random quest '&c{quest}&7' from category '&c{category}&7' for player &c{player}&7."),

    // Other
    UI_PLACEHOLDERS_TRUE("messages.ui-placeholder-completed-true", "true"),
    UI_PLACEHOLDERS_FALSE("messages.ui-placeholder-completed-false", "false"),
    UI_PLACEHOLDERS_TRUNCATED("messages.ui-placeholder-truncated", " +{amount} more"),
    UI_PLACEHOLDERS_NO_TIME_LIMIT("messages.ui-placeholder-no-time-limit", "No time limit"),
    PLACEHOLDERAPI_TRUE("messages.placeholderapi-true", "true"),
    PLACEHOLDERAPI_FALSE("messages.placeholderapi-false", "false"),
    PLACEHOLDERAPI_NO_TRACKED_QUEST("messages.placeholderapi-no-tracked-quest", "No tracked quest"),
    PLACEHOLDERAPI_QUEST_NOT_STARTED("messages.placeholderapi-quest-not-started", "Quest not started"),
    PLACEHOLDERAPI_NO_COOLDOWN("messages.placeholderapi-no-cooldown", "No cooldown"),
    PLACEHOLDERAPI_NO_TIME_LIMIT("messages.placeholderapi-no-time-limit", "No time limit"),
    PLACEHOLDERAPI_DATA_NOT_LOADED("messages.placeholderapi-data-not-loaded", "Data not loaded");

    static {
        plugin = BukkitQuestsPlugin.getPlugin(BukkitQuestsPlugin.class);
    }

    private static final BukkitQuestsPlugin plugin;

    private final String path;
    private final String def;

    Messages(String path) {
        this.path = path;
        this.def = path;
    }

    Messages(String path, String def) {
        this.path = path;
        this.def = def;
    }

    public String getMessageLegacyColor() {
        return Chat.legacyColor(getMessage());
    }

    public String getMessage() {
        String message = plugin.getQuestsConfig().getString(path);
        if (message.equals(path)) message = def;

        return message;
    }

    public boolean send(CommandSender target, String... substitutions) {
        return send(getMessage(), target, substitutions);
    }

    public static boolean send(String message, CommandSender target, String... substitutions) {
        Chat.send(target, message, true, substitutions);
        return true;
    }
}
