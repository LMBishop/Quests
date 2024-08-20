package com.leonardobishop.quests.bukkit.hook.vault;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class VaultHook implements AbstractVaultHook {

    private final BukkitQuestsPlugin plugin;
    private Economy economy = null;

    public VaultHook(final @NotNull BukkitQuestsPlugin plugin) {
        this.plugin = plugin;

        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
        } catch (final ClassNotFoundException e) {
            return;
        }

        final Plugin vaultPlugin = Bukkit.getServer()
                .getPluginManager()
                .getPlugin("Vault");
        if (vaultPlugin == null) {
            return;
        }

        final RegisteredServiceProvider<Economy> esp = Bukkit.getServer()
                .getServicesManager()
                .getRegistration(Economy.class);
        if (esp == null) {
            return;
        }

        this.economy = esp.getProvider();

        // Log that we hooked successfully
        this.plugin.getLogger().info("Successfully hooked into " + esp.getPlugin().getName() + " economy.");
    }

    @Override
    public void depositPlayer(final @NotNull OfflinePlayer player, final double amount) {
        if (amount <= 0) {
            this.plugin.getQuestsLogger().debug("Tried to deposit Vault reward of " + amount + " to "
                    + player.getName() + " account, however the amount was not a positive number.");
            return;
        }

        if (this.economy == null) {
            this.plugin.getQuestsLogger().debug("Tried to deposit Vault reward of " + amount + " to "
                    + player.getName() + " account, however the economy could not be found.");
            return;
        }

        final EconomyResponse response = this.economy.depositPlayer(player, amount);
        this.plugin.getQuestsLogger().debug("Deposited Vault reward of " + amount + " to "
                + player.getName() + " account. Response: " + this.responseToString(response) + ".");
    }

    @Contract(pure = true)
    private @NotNull String responseToString(final @NotNull EconomyResponse response) {
        return "EconomyResponse{"
                + "amount=" + response.amount
                + ", balance=" + response.balance
                + ", type=" + response.type.name()
                + ", errorMessage='"
                + response.errorMessage
                + "'}";
    }
}
