package com.leonardobishop.quests.bukkit.hook.vault.rewards;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.hook.papi.AbstractPlaceholderAPIHook;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

final class PlaceholderVaultReward extends VaultReward {

    private final String rewardString;

    PlaceholderVaultReward(final @NotNull BukkitQuestsPlugin plugin, final @NotNull String rewardString) {
        super(plugin);

        this.rewardString = rewardString;
    }

    @Override
    public double getRewardValue(final @NotNull Player player) {
        final AbstractPlaceholderAPIHook hook = this.plugin.getPlaceholderAPIHook();

        if (hook == null) {
            this.plugin.getLogger().warning("Could not give '" + this.rewardString + "' vault reward to " + player.getName() + ". No PlaceholderAPI hook found!");
            return 0.0d;
        }

        final String papiRewardString = this.plugin.getPlaceholderAPIHook().replacePlaceholders(player, this.rewardString);

        final double vaultReward;
        try {
            vaultReward = Double.parseDouble(papiRewardString);
        } catch (final NumberFormatException e) {
            this.plugin.getLogger().warning("Could not give '" + this.rewardString + "' (PAPI: '" + papiRewardString + "') vault reward to " + player.getName() + ". Invalid double format!");
            return 0.0d;
        }

        return vaultReward;
    }
}
