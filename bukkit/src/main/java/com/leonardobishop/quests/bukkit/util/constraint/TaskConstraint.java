package com.leonardobishop.quests.bukkit.util.constraint;

public enum TaskConstraint {
    WORLD(0b00000001),
    BIOME(0x00000010);

    private final int value;

    TaskConstraint(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
