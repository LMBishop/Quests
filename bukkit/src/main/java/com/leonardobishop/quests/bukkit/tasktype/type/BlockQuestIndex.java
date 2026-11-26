package com.leonardobishop.quests.bukkit.tasktype.type;

import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.tasktype.TaskType;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class BlockQuestIndex {
    private final TaskType type;
    private volatile Snapshot snapshot = new Snapshot(-1, List.of(), Map.of());

    BlockQuestIndex(TaskType type) {
        this.type = type;
    }

    List<Quest> candidates(Material material) {
        Snapshot current = snapshot;
        if (current.revision() != type.getQuestRegistrationRevision()) {
            rebuild();
            current = snapshot;
        }
        return current.byMaterial().getOrDefault(material, current.unrestricted());
    }

    void rebuild() {
        Map<Quest, Set<Material>> restricted = new LinkedHashMap<>();
        List<Quest> wildcard = new ArrayList<>();
        Set<Material> materials = EnumSet.noneOf(Material.class);
        List<Quest> quests = type.getRegisteredQuests();
        for (Quest quest : quests) {
            Set<Material> blocks = blocksOf(quest);
            if (blocks == null) {
                wildcard.add(quest);
            } else {
                restricted.put(quest, blocks);
                materials.addAll(blocks);
            }
        }

        Map<Material, List<Quest>> index = new EnumMap<>(Material.class);
        for (Material material : materials) {
            index.put(material, quests.stream()
                    .filter(quest -> !restricted.containsKey(quest) || restricted.get(quest).contains(material))
                    .toList());
        }
        snapshot = new Snapshot(type.getQuestRegistrationRevision(), List.copyOf(wildcard), index);
    }

    private Set<Material> blocksOf(Quest quest) {
        Set<Material> materials = EnumSet.noneOf(Material.class);
        for (Task task : quest.getTasksOfType(type.getType())) {
            Object value = task.getConfigValue(task.hasConfigKey("block") ? "block" : "blocks");
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
                int separator = name.indexOf(':');
                Material material = Material.getMaterial(separator < 0 ? name : name.substring(0, separator));
                if (material != null) {
                    materials.add(material);
                }
            }
        }
        return materials;
    }

    private record Snapshot(long revision, List<Quest> unrestricted, Map<Material, List<Quest>> byMaterial) {
    }
}
