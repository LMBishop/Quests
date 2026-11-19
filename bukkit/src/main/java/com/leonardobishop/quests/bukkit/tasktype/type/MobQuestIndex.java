package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.tasktype.TaskType;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class MobQuestIndex {
    private final TaskType type;
    private volatile Snapshot snapshot = new Snapshot(-1, List.of(), Map.of());

    MobQuestIndex(TaskType type) {
        this.type = type;
    }

    List<Quest> candidates(EntityType mob) {
        Snapshot current = snapshot;
        if (current.revision() != type.getQuestRegistrationRevision()) {
            rebuild();
            current = snapshot;
        }
        return current.byMob().getOrDefault(mob, current.unrestricted());
    }

    void rebuild() {
        long revision = type.getQuestRegistrationRevision();
        List<Quest> quests = type.getRegisteredQuests();
        Map<Quest, Set<EntityType>> restricted = new HashMap<>();
        List<Quest> unrestricted = new ArrayList<>();
        Set<EntityType> mobs = EnumSet.noneOf(EntityType.class);
        for (Quest quest : quests) {
            Set<EntityType> types = mobsOf(quest);
            if (types == null) {
                unrestricted.add(quest);
            } else {
                restricted.put(quest, types);
                mobs.addAll(types);
            }
        }

        Map<EntityType, List<Quest>> index = new EnumMap<>(EntityType.class);
        for (EntityType mob : mobs) {
            index.put(mob, quests.stream()
                    .filter(quest -> !restricted.containsKey(quest) || restricted.get(quest).contains(mob))
                    .toList());
        }
        snapshot = new Snapshot(revision, List.copyOf(unrestricted), index);
    }

    private Set<EntityType> mobsOf(Quest quest) {
        Set<EntityType> mobs = EnumSet.noneOf(EntityType.class);
        for (Task task : quest.getTasksOfType(type.getType())) {
            Object value = task.getConfigValue(task.hasConfigKey("mob") ? "mob" : "mobs");
            List<?> names;
            if (value instanceof String name) {
                names = List.of(name);
            } else if (value instanceof List<?> list) {
                names = list;
            } else {
                return null;
            }
            for (Object entry : names) {
                if (!(entry instanceof String name)) {
                    return null;
                }
                try {
                    mobs.add(EntityType.valueOf(name));
                } catch (IllegalArgumentException invalidType) {
                    // Leave invalid filters to the regular task checks.
                    return null;
                }
            }
        }
        return mobs;
    }

    private record Snapshot(long revision, List<Quest> unrestricted, Map<EntityType, List<Quest>> byMob) {
    }
}
