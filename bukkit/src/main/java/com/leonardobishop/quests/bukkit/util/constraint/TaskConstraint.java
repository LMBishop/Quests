package com.leonardobishop.quests.bukkit.util.constraint;

public enum TaskConstraint {
    WORLD(0x00000001);

    private final int value;

    TaskConstraint(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
