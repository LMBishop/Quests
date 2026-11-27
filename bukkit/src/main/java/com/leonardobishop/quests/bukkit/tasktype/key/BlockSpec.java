package com.leonardobishop.quests.bukkit.tasktype.key;

import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A parsed block entry in the form {@code MATERIAL} or {@code MATERIAL:data}.
 *
 * @param material the material
 * @param data     legacy data value given inline, or null if none
 */
@NullMarked
public record BlockSpec(Material material, @Nullable Integer data) {

    @Override
    public String toString() {
        return this.data != null ? this.material + ":" + this.data : this.material.toString();
    }
}
