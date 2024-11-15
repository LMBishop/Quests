package com.leonardobishop.quests.bukkit.hook.vault.rewards;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

final class DummyVaultReward extends VaultReward {

    static final DummyVaultReward INSTANCE = new DummyVaultReward();

    DummyVaultReward() {
        // temporarily ignore it
        //noinspection DataFlowIssue
        super(null);
    }

    @Override
    public double getRewardValue(final @NotNull Player player) {
        return 0.0d;
    }

    @Override
    public void give(final @NotNull Player player) {
    }
}
