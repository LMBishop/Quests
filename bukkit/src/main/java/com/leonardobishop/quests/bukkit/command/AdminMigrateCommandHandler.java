package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.storage.MySqlStorageProvider;
import com.leonardobishop.quests.bukkit.storage.YamlStorageProvider;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
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

            if (fromProvider.getName().equals("yaml") && toProvider.getName().equals("yaml")) {
                //TODO check mysql databases aren't the same as well
                sender.sendMessage(ChatColor.RED + "Refusing to migrate from 'yaml' to 'yaml'.");
                sender.sendMessage(ChatColor.RED + "Please see the migrate_data.yml file, or the wiki, for instructions.");
                return;
            }

            long startTime = System.currentTimeMillis();
            sender.sendMessage(ChatColor.GRAY + "Performing migration...");
            migrationInProgress.set(true);
            plugin.getScheduler().doAsync(() -> {
                try {
                    sender.sendMessage(ChatColor.GRAY + "Initialising storage provider '" + fromProvider.getName() + "'...");
                    fromProvider.init();
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "An error occurred while initializing '" + fromProvider.getName() + "' storage provider.");
                    return;
                }

                try {
                    sender.sendMessage(ChatColor.GRAY + "Initialising storage provider '" + toProvider.getName() + "'...");
                    toProvider.init();
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "An error occurred while initializing '" + toProvider.getName() + "' storage provider.");
                    return;
                }

                sender.sendMessage(ChatColor.GRAY + "Loading quest progress files from '" + fromProvider.getName() + "'...");
                List<QuestProgressFile> files = fromProvider.loadAllProgressFiles();
                sender.sendMessage(ChatColor.GRAY.toString() + files.size() + " files loaded.");

                for (QuestProgressFile file : files) {
                    file.setModified(true);
                }

                sender.sendMessage(ChatColor.GRAY + "Writing quest progress files to '" + toProvider.getName() + "'...");
                toProvider.saveAllProgressFiles(files);
                sender.sendMessage(ChatColor.GRAY + "Done.");

                try {
                    sender.sendMessage(ChatColor.GRAY + "Shutting down storage provider '" + fromProvider.getName() + "'...");
                    fromProvider.shutdown();
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "An error occurred while shutting down '" + fromProvider.getName() + "' storage provider.");
                }

                try {
                    sender.sendMessage(ChatColor.GRAY + "Shutting down storage provider '" + toProvider.getName() + "'...");
                    toProvider.shutdown();
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "An error occurred while shutting down '" + toProvider.getName() + "' storage provider.");
                }

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

    private StorageProvider getStorageProvider(ConfigurationSection configurationSection) {
        String configuredProvider = configurationSection.getString("provider", "yaml");
        StorageProvider storageProvider;
        switch (configuredProvider.toLowerCase()) {
            default:
            case "yaml":
                storageProvider = new YamlStorageProvider(plugin);
                break;
            case "mysql":
                ConfigurationSection section = configurationSection.getConfigurationSection("database-settings");
                storageProvider = new MySqlStorageProvider(plugin, section);
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
