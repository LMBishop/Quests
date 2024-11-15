package com.leonardobishop.quests.bukkit.hook.vault.rewards;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class VaultReward {

    final BukkitQuestsPlugin plugin;

    VaultReward(final @NotNull BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract double getRewardValue(final @NotNull Player player);

    public void give(final @NotNull Player player) {
        this.plugin.getVaultHook().depositPlayer(player, this.getRewardValue(player));
    }

    public static @NotNull VaultReward parse(final @NotNull BukkitQuestsPlugin plugin, final @Nullable String str) {
        if (str == null) {
            return DummyVaultReward.INSTANCE;
        }

        if (str.indexOf('%') == -1) {
            return parseNumeric(plugin, str);
        }

        return new PlaceholderVaultReward(plugin, str);
    }

    private static @NotNull VaultReward parseNumeric(final @NotNull BukkitQuestsPlugin plugin, final @NotNull String str) {
        final double value;

        try {
            value = Double.parseDouble(str);
        } catch (final NumberFormatException e) {
            plugin.getLogger().warning("Could not parse '" + str + "' vault reward. Invalid double format!");
            return DummyVaultReward.INSTANCE;
        }

        return new NumericVaultReward(plugin, value);
    }
}
