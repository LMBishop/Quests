package com.leonardobishop.quests.bukkit.util.constraint;

import org.jspecify.annotations.NullMarked;

import java.util.EnumSet;

@NullMarked
public final class TaskConstraintSet {

    public static final EnumSet<TaskConstraint> ALL = EnumSet.allOf(TaskConstraint.class);
    public static final EnumSet<TaskConstraint> NONE = EnumSet.noneOf(TaskConstraint.class);
}
