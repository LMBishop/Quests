package com.leonardobishop.quests.bukkit.hook.papi.data;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.filters.QuestProgressFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum CommonPlaceholderType {
    ALL("all", "a") {
        @Override
        public int getCount(final @NotNull BukkitQuestsPlugin plugin, final @NotNull QPlayer player) {
            return plugin.getQuestManager().getQuests().size();
        }
    },
    COMPLETED("completed", "c") {
        @Override
        public int getCount(final @NotNull BukkitQuestsPlugin plugin, final @NotNull QPlayer player) {
            return player.getQuestProgressFile().getAllQuestsFromProgressCount(QuestProgressFilter.COMPLETED);
        }
    },
    COMPLETED_BEFORE("completedbefore", "cb") {
        @Override
        public int getCount(final @NotNull BukkitQuestsPlugin plugin, final @NotNull QPlayer player) {
            return player.getQuestProgressFile().getAllQuestsFromProgressCount(QuestProgressFilter.COMPLETED_BEFORE);
        }
    },
    STARTED("started", "s") {
        @Override
        public int getCount(final @NotNull BukkitQuestsPlugin plugin, final @NotNull QPlayer player) {
            return player.getQuestProgressFile().getAllQuestsFromProgressCount(QuestProgressFilter.STARTED);
        }
    },
    CATEGORIES("categories") {
        @Override
        public int getCount(final @NotNull BukkitQuestsPlugin plugin, final @NotNull QPlayer player) {
            return plugin.getQuestManager().getCategories().size();
        }
    };

    private final String[] names;

    CommonPlaceholderType(final @NotNull String @NotNull ... names) {
        this.names = names;
    }

    public abstract int getCount(final @NotNull BukkitQuestsPlugin plugin, final @NotNull QPlayer player);

    public @NotNull String @NotNull [] getNames() {
        return this.names;
    }

    private static final CommonPlaceholderType[] VALUES = CommonPlaceholderType.values();

    private static final Map<String, CommonPlaceholderType> byName = new HashMap<>(CommonPlaceholderType.VALUES.length) {{
        for (final CommonPlaceholderType placeholderType : CommonPlaceholderType.VALUES) {
            for (final String name : placeholderType.getNames()) {
                this.put(name, placeholderType);
            }
        }
    }};

    public static @Nullable CommonPlaceholderType getByName(final @NotNull String name) {
        return CommonPlaceholderType.byName.get(name);
    }
}
