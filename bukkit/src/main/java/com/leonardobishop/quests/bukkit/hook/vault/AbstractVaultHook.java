package com.leonardobishop.quests.bukkit.hook.vault;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public interface AbstractVaultHook {

    void depositPlayer(final @NotNull OfflinePlayer player, final double amount);
}
