package com.leonardobishop.quests.bukkit.util.constraint;

import org.jetbrains.annotations.NotNull;

public final class TaskConstraintSet {

    public static final TaskConstraintSet ALL = new TaskConstraintSet(TaskConstraint.values());
    public static final TaskConstraintSet NONE = new TaskConstraintSet();
    private final int rawValue;

    public TaskConstraintSet(final @NotNull TaskConstraint... constraints) {
        int rawValue = 0;
        for (final TaskConstraint constraint : constraints) {
            rawValue |= constraint.getValue();
        }
        this.rawValue = rawValue;
    }

    public TaskConstraintSet(final @NotNull TaskConstraint constraint) {
        this.rawValue = constraint.getValue();
    }

    public TaskConstraintSet(final int rawValue) {
        this.rawValue = rawValue;
    }

    public boolean contains(final @NotNull TaskConstraint constraint) {
        return (this.rawValue & constraint.getValue()) != 0;
    }
}
