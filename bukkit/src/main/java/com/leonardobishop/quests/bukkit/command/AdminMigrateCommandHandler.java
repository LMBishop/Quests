package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.storage.ModernMySQLStorageProvider;
import com.leonardobishop.quests.bukkit.storage.ModernYAMLStorageProvider;
import com.leonardobishop.quests.common.player.QPlayerData;
import com.leonardobishop.quests.common.storage.StorageProvider;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdminMigrateCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    private final AtomicBoolean migrationInProgress;

    public AdminMigrateCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
        this.migrationInProgress = new AtomicBoolean(false);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        File dataMigrateFile = new File(plugin.getDataFolder(), "migrate_data.yml");

        if (migrationInProgress.get()) {
            sender.sendMessage(ChatColor.RED + "A migration is already in progress.");
            return;
        }

        if (args.length == 3 && args[2].equalsIgnoreCase("execute")) {
            if (!dataMigrateFile.exists()) {
                sender.sendMessage(ChatColor.RED + "Please run '/quests admin migratedata' first.");
                return;
            }

            YamlConfiguration configuration;
            try {
                configuration = YamlConfiguration.loadConfiguration(dataMigrateFile);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "An error occurred while loading the data migration file.");
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "See server console for more details.");
                return;
            }

            if (!configuration.getBoolean("ready")) {
                sender.sendMessage(ChatColor.RED + "The 'ready' flag has not been set.");
                sender.sendMessage(ChatColor.RED + "Please see the migrate_data.yml file, or the wiki, for instructions.");
                return;
            }

            ConfigurationSection fromConfiguration = configuration.getConfigurationSection("from");
            ConfigurationSection toConfiguration = configuration.getConfigurationSection("to");

            if (fromConfiguration == null || toConfiguration == null) {
                sender.sendMessage(ChatColor.RED + "The 'from' and 'to' sections have not been configured.");
                sender.sendMessage(ChatColor.RED + "Please see the migrate_data.yml file, or the wiki, for instructions.");
                return;
            }

            StorageProvider fromProvider = getStorageProvider(fromConfiguration);
            StorageProvider toProvider = getStorageProvider(toConfiguration);

            if (fromProvider.isSimilar(toProvider)) {
                sender.sendMessage(ChatColor.RED + "Refusing to migrate from and to identical database! " +
                        "Your configured storage providers effectively point to the same data source.");
                return;
            }

            long startTime = System.currentTimeMillis();
            sender.sendMessage(ChatColor.GRAY + "Performing migration...");
            migrationInProgress.set(true);
            plugin.getScheduler().doAsync(() -> {
                if (!initProvider(sender, fromProvider) || !initProvider(sender, toProvider)) {
                    sender.sendMessage(ChatColor.DARK_RED + "Migration aborted.");
                    return;
                }

                sender.sendMessage(ChatColor.GRAY + "Loading quest progress files from '" + fromProvider.getName() + "'...");
                List<QPlayerData> files = fromProvider.loadAllPlayerData();
                sender.sendMessage(ChatColor.GRAY.toString() + files.size() + " files loaded.");

                for (QPlayerData file : files) {
                    file.setModified(true);
                }

                sender.sendMessage(ChatColor.GRAY + "Writing quest progress files to '" + toProvider.getName() + "'...");
                toProvider.saveAllPlayerData(files);
                sender.sendMessage(ChatColor.GRAY + "Done.");

                shutdownProvider(sender, fromProvider);
                shutdownProvider(sender, toProvider);

                long endTime = System.currentTimeMillis();
                sender.sendMessage(ChatColor.GREEN + "Migration complete. Took " + String.format("%.3f", (endTime - startTime) / 1000f) + "s.");

                configuration.set("ready", false);
                try {
                    configuration.save(dataMigrateFile);
                } catch (IOException ignored) { }
                migrationInProgress.set(false);
            });
            return;
        }

        if (!dataMigrateFile.exists()) {
            plugin.writeResourceToFile("resources/bukkit/migrate_data.yml", dataMigrateFile);
        }
        sender.sendMessage(ChatColor.GRAY + "A file has been generated at /plugins/Quests/migrate_data.yml.");
        sender.sendMessage(ChatColor.GRAY + "Please see this file, or the wiki, for further instructions.");
    }

    private void shutdownProvider(CommandSender sender, StorageProvider provider) {
        try {
            sender.sendMessage(ChatColor.GRAY + "Shutting down storage provider '" + provider.getName() + "'...");
            provider.shutdown();
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred while shutting down '" + provider.getName() + "' storage provider. " +
                    "See server console for more details.");
            e.printStackTrace();
        }
    }

    private boolean initProvider(CommandSender sender, StorageProvider provider) {
        try {
            sender.sendMessage(ChatColor.GRAY + "Initialising storage provider '" + provider.getName() + "'...");
            provider.init();
            return true;
        } catch (Exception e) {
            migrationInProgress.set(false);
            sender.sendMessage(ChatColor.RED + "An error occurred while initializing '" + provider.getName() + "' storage provider. " +
                    "See server console for more details.");
            e.printStackTrace();
            return false;
        }
    }

    private StorageProvider getStorageProvider(ConfigurationSection configurationSection) {
        String configuredProvider = configurationSection.getString("provider", "yaml");
        StorageProvider storageProvider;
        switch (configuredProvider.toLowerCase()) {
            default:
            case "yaml":
                storageProvider = new ModernYAMLStorageProvider(plugin);
                break;
            case "mysql":
                ConfigurationSection section = configurationSection.getConfigurationSection("database-settings");
                storageProvider = new ModernMySQLStorageProvider(plugin, section);
        }
        return storageProvider;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return TabHelper.matchTabComplete(args[2], Collections.singletonList("execute"));
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
