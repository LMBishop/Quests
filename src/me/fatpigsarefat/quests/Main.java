package me.fatpigsarefat.quests;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import me.fatpigsarefat.quests.commands.CommandQuest;
import me.fatpigsarefat.quests.events.BlockBreak;
import me.fatpigsarefat.quests.events.BlockPlace;
import me.fatpigsarefat.quests.events.InventoryInteract;
import me.fatpigsarefat.quests.events.MobKill;
import me.fatpigsarefat.quests.events.PlayerKill;
import me.fatpigsarefat.quests.title.Title;
import me.fatpigsarefat.quests.title.Title_v1_10_R1;
import me.fatpigsarefat.quests.title.Title_v1_11_R1;
import me.fatpigsarefat.quests.title.Title_v1_8_R1;
import me.fatpigsarefat.quests.title.Title_v1_8_R2;
import me.fatpigsarefat.quests.title.Title_v1_8_R3;
import me.fatpigsarefat.quests.title.Title_v1_9_R1;
import me.fatpigsarefat.quests.title.Title_v1_9_R2;

public class Main extends JavaPlugin {

	public Title title;
	public boolean titleEnabled = true;
	public static Main instance;
	CommandQuest quest = new CommandQuest(this);

	public void onEnable() {
		instance = this;
		new BlockBreak(this);
		new BlockPlace(this);
		new MobKill(this);
		new PlayerKill(this);
		new InventoryInteract(this);
		getCommand("quest").setExecutor(new CommandQuest(this));
		// TODO getCommand("questcreate").setExecutor(new
		// CommandQuestcreate(this));
		new CommandQuest(this);
		// TODO get my act together and actualy make these inventory quests >.>
		// new Inventory(this).runTaskTimer(this, 20L, 20L);
		saveDefaultConfig();
		File d = new File(this.getDataFolder() + File.separator + "data.yml");
		if (!d.exists()) {
			try {
				d.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		YamlConfiguration data = YamlConfiguration.loadConfiguration((File) d);
		data.options().copyDefaults(true);
		if (setupTitle()) {
			getLogger().info("Quests setup was successful!");
		} else {
			getLogger().info("Your NMS version is not compatible with supported versions.");
			getLogger().info("Titles in this version will be disabled!");
			getLogger().info("Quests is compatible with: v1_8_R2, v1_8_R3, v1_9_R1, v1_9_R2, v1_10_R1, v1_11_R1");
			getLogger().info("Quests setup was successful with 1 error!");
			titleEnabled = false;
		}
		cooldownTimer();
	}

	// Thanks that guy on the wiki who suggested I do this instead of
	// reflection.
	private boolean setupTitle() {
		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		boolean success = false;
		getLogger().info("Your server is running version " + version);
		if (version.equals("v1_8_R3")) {
			title = new Title_v1_8_R3();
			success = true;
		} else if (version.equals("v1_8_R2")) {
			title = new Title_v1_8_R2();
			success = true;
		} else if (version.equals("v1_8_R1")) {
			title = new Title_v1_8_R1();
			success = true;
		} else if (version.equals("v1_9_R2")) {
			title = new Title_v1_9_R2();
			success = true;
		} else if (version.equals("v1_9_R1")) {
			title = new Title_v1_9_R1();
			success = true;
		} else if (version.equals("v1_10_R1")) {
			title = new Title_v1_10_R1();
			success = true;
		} else if (version.equals("v1_11_R1")) {
			title = new Title_v1_11_R1();
			success = true;
		}
		return success;
	}

	public void cooldownTimer() {
		BukkitScheduler cooldownTimer = getServer().getScheduler();
		cooldownTimer.scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				File b = new File(getDataFolder() + File.separator + "data.yml");
				YamlConfiguration data = YamlConfiguration.loadConfiguration(b);
				if (data.contains("progress")) {
					for (String s : data.getConfigurationSection("progress").getKeys(false)) {
						if (data.contains("progress." + s + ".quests-cooldown")) {
							for (String st : data.getConfigurationSection("progress." + s + ".quests-cooldown")
									.getKeys(false)) {
								int cooldwnTime = data.getInt("progress." + s + ".quests-cooldown." + st);
								cooldwnTime--;
								if (cooldwnTime <= 0) {
									data.set("progress." + s + ".quests-cooldown." + st, null);
								} else {
									data.set("progress." + s + ".quests-cooldown." + st, cooldwnTime);
								}
							}
						}
					}
					try {
						data.save(b);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}, 0L, 1200L);
	}

}