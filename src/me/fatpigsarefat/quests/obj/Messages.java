package me.fatpigsarefat.quests.obj;

import me.fatpigsarefat.quests.Quests;
import org.bukkit.ChatColor;

public enum Messages {

    QUEST_START("messages.quest-start"),
    QUEST_COMPLETE("messages.quest-complete"),
    QUEST_CANCEL("messages.quest-cancel"),
    QUEST_START_LIMIT("messages.quest-start-limit"),
    QUEST_START_DISABLED("messages.quest-start-disabled"),
    QUEST_START_LOCKED("messages.quest-start-locked"),
    QUEST_START_COOLDOWN("messages.quest-start-cooldown"),
    QUEST_CANCEL_NOTSTARTED("messages.quest-cancel-notstarted"),
    QUEST_UPDATER("messages.quest-updater"),
    COMMAND_QUEST_START_DOESNTEXIST("messages.command-quest-start-doesntexist"),
    COMMAND_QUEST_OPENCATEGORY_ADMIN_SUCCESS("messages.command-quest-opencategory-admin-success"),
    COMMAND_QUEST_OPENQUESTS_ADMIN_SUCCESS("messages.command-quest-openquests-admin-success"),
    COMMAND_QUEST_ADMIN_PLAYERNOTFOUND("messages.command-quest-admin-playernotfound"),
    COMMAND_CATEGORY_OPEN_DOESNTEXIST("messages.command-category-open-doesntexist"),
    COMMAND_CATEGORY_OPEN_DISABLED("messages.command-category-open-disabled"),
    COMMAND_QUEST_START_ADMIN_SUCCESS("messages.command-quest-start-admin-success"),
    COMMAND_TASKVIEW_ADMIN_FAIL("messages.command-taskview-admin-fail"),
    COMMAND_QUEST_START_ADMIN_FAIL("messages.command-quest-start-admin-fail"),
    TITLE_QUEST_START_TITLE("titles.quest-start.title"),
    TITLE_QUEST_START_SUBTITLE("titles.quest-start.subtitle"),
    TITLE_QUEST_COMPLETE_TITLE("titles.quest-complete.title"),
    TITLE_QUEST_COMPLETE_SUBTITLE("titles.quest-complete.subtitle"),
    BETA_REMINDER("messages.beta-reminder"),
    COMMAND_QUEST_ADMIN_LOADDATA("messages.command-quest-admin-loaddata"),
    COMMAND_QUEST_ADMIN_NODATA("messages.command-quest-admin-nodata"),
    COMMAND_QUEST_ADMIN_FULLRESET("messages.command-quest-admin-fullreset"),
    COMMAND_QUEST_ADMIN_START_FAILLOCKED("messages.command-quest-admin-start-faillocked"),
    COMMAND_QUEST_ADMIN_START_FAILCOOLDOWN("messages.command-quest-admin-start-failcooldown"),
    COMMAND_QUEST_ADMIN_START_FAILCOMPLETE("messages.command-quest-admin-start-failcomplete"),
    COMMAND_QUEST_ADMIN_START_FAILLIMIT("messages.command-quest-admin-start-faillimit"),
    COMMAND_QUEST_ADMIN_START_SUCCESS("messages.command-quest-admin-start-success"),
    COMMAND_QUEST_ADMIN_COMPLETE_SUCCESS("messages.command-quest-admin-complete-success"),
    COMMAND_QUEST_ADMIN_RESET_SUCCESS("messages.command-quest-admin-reset-success");

    private String path;

    Messages(String path) {
        this.path = path;
    }

    public String getMessage() {
        if (Quests.getInstance().getConfig().contains(path)) {
            String message = Quests.getInstance().getConfig().getString(path);
            if (message != null) {
                return ChatColor.translateAlternateColorCodes('&', message);
            }
        }
        return path;
    }

}
