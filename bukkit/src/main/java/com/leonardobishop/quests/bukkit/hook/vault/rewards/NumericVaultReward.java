package com.leonardobishop.quests.bukkit.hook.vault.rewards;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

final class NumericVaultReward extends VaultReward {

    private final double value;

    NumericVaultReward(final @NotNull BukkitQuestsPlugin plugin, final double value) {
        super(plugin);

        this.value = value;
    }

    @Override
    public double getRewardValue(final @NotNull Player player) {
        return this.value;
    }
}
