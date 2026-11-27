package com.leonardobishop.quests.bukkit.item.requirement;

import com.leonardobishop.quests.bukkit.item.QuestItem;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A condition an {@link ItemStack} must satisfy, e.g. the item a player must hold for a task to progress.
 * <p>
 * Requirements are parsed once when quests are loaded (see {@link ItemRequirementParser}); matching only
 * compares already parsed values.
 */
@NullMarked
@FunctionalInterface
public interface ItemRequirement {

    /**
     * @param item the item to test; null (or air) never matches
     * @return whether the item satisfies this requirement
     */
    boolean matches(@Nullable ItemStack item);

    /**
     * @return a requirement satisfied if any of the given requirements is
     */
    static ItemRequirement anyOf(final List<? extends ItemRequirement> requirements) {
        final List<ItemRequirement> copy = List.copyOf(requirements);
        if (copy.size() == 1) {
            return copy.getFirst();
        }

        return new ItemRequirement() {
            @Override
            public boolean matches(final @Nullable ItemStack item) {
                for (final ItemRequirement requirement : copy) {
                    if (requirement.matches(item)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String toString() {
                return "any of " + copy;
            }
        };
    }

    /**
     * Adapts a legacy {@code item} / {@code exact-match} option pair.
     */
    static ItemRequirement legacy(final QuestItem questItem, final boolean exactMatch) {
        Objects.requireNonNull(questItem, "questItem cannot be null");

        return new ItemRequirement() {
            @Override
            public boolean matches(final @Nullable ItemStack item) {
                return item != null && questItem.compareItemStack(item, exactMatch);
            }

            @Override
            public String toString() {
                return "legacy item " + questItem.getItemStack().getType() + (exactMatch ? " (exact)" : "");
            }
        };
    }

    /**
     * Adapts a legacy {@code item} option which only compares the material.
     */
    static ItemRequirement legacyMaterial(final QuestItem questItem) {
        Objects.requireNonNull(questItem, "questItem cannot be null");

        return new ItemRequirement() {
            @Override
            public boolean matches(final @Nullable ItemStack item) {
                return item != null && item.getType() == questItem.getItemStack().getType();
            }

            @Override
            public String toString() {
                return "legacy material " + questItem.getItemStack().getType();
            }
        };
    }

    /**
     * Picks the requirement to use for a task: the {@code *-requirement} option if configured,
     * otherwise the legacy {@code item} option, otherwise none.
     */
    static @Nullable ItemRequirement resolve(final @Nullable ItemRequirement requirement, final @Nullable QuestItem legacyItem, final boolean exactMatch) {
        if (requirement != null) {
            return requirement;
        }

        return legacyItem != null ? legacy(legacyItem, exactMatch) : null;
    }
}
