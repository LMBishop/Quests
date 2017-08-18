package me.fatpigsarefat.quests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import me.fatpigsarefat.quests.commands.CommandQuest;
import me.fatpigsarefat.quests.commands.CommandQuestdesign;
import me.fatpigsarefat.quests.events.InventoryInteract;
import me.fatpigsarefat.quests.questhandlers.BlockBreak;
import me.fatpigsarefat.quests.questhandlers.BlockPlace;
import me.fatpigsarefat.quests.questhandlers.Inventory;
import me.fatpigsarefat.quests.questhandlers.MobKill;
import me.fatpigsarefat.quests.questhandlers.PlayerKill;
import me.fatpigsarefat.quests.questhandlers.Skyblock;
import me.fatpigsarefat.quests.questhandlers.TimePlayed;
import me.fatpigsarefat.quests.title.Title;
import me.fatpigsarefat.quests.title.Title_v1_10_R1;
import me.fatpigsarefat.quests.title.Title_v1_11_R1;
import me.fatpigsarefat.quests.title.Title_v1_12_R1;
import me.fatpigsarefat.quests.title.Title_v1_8_R1;
import me.fatpigsarefat.quests.title.Title_v1_8_R2;
import me.fatpigsarefat.quests.title.Title_v1_8_R3;
import me.fatpigsarefat.quests.title.Title_v1_9_R1;
import me.fatpigsarefat.quests.title.Title_v1_9_R2;
import me.fatpigsarefat.quests.utils.ExternalPlaceholders;
import me.fatpigsarefat.quests.utils.Messages;
import me.fatpigsarefat.quests.utils.Quest;
import me.fatpigsarefat.quests.utils.QuestData;
import me.fatpigsarefat.quests.utils.QuestManager;
import me.fatpigsarefat.quests.utils.QuestType;
import me.fatpigsarefat.quests.utils.SelectorType;

public class Quests extends JavaPlugin {

	private Title title;
	private boolean titleEnabled = true;
	private static Quests instance;
	private boolean debug = false;
	private HashMap<String, String> alternateNamesForBlocks = new HashMap<String, String>();
	private QuestManager questManager;
	private QuestData questData;
	private boolean uskyblockEnabled = false;
	private boolean askyblockEnabled = false;

	public void onEnable() {
		instance = this;
		saveDefaultConfig();

		questManager = new QuestManager();
		questData = new QuestData();
		reloadQuests();

		new BlockBreak(this);
		new BlockPlace(this);
		new MobKill(this);
		new PlayerKill(this);
		new InventoryInteract(this);
		getCommand("quest").setExecutor(new CommandQuest());
		getCommand("questguidesigner").setExecutor(new CommandQuestdesign());
		// TODO getCommand("questcreate").setExecutor(new
		// CommandQuestcreate(this));
		new Inventory(this).runTaskTimer(this, 50L, 50L);
		new Skyblock().runTaskTimer(this, 200L, 200L);
		new TimePlayed().runTaskTimer(this, 1200L, 1200L);
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
			getLogger().info(
					"Quests is compatible with: v1_8_R1, v1_8_R2, v1_8_R3, v1_9_R1, v1_9_R2, v1_10_R1, v1_11_R1, v1_12_R1");
			getLogger().info("Quests setup was successful with errors!");
			titleEnabled = false;
		}
		cooldownTimer();
		alternateBlockNames();
		
		fullStartUp();
	}

	public void reloadQuests() {
		questManager.unregisterAllQuests();
		for (String questId : getConfig().getConfigurationSection("quests").getKeys(false)) {
			QuestType questType;
			if (getConfig().getString("quests." + questId + ".type").contains(":")) {
				String[] parts = getConfig().getString("quests." + questId + ".type").split(":");
				questType = QuestType.fromString(parts[0]);
			} else {
				questType = QuestType.fromString(getConfig().getString("quests." + questId + ".type"));
			}
			boolean redoable = getConfig().getBoolean("quests." + questId + ".redoable");
			boolean cooldownEnabled = getConfig().getBoolean("quests." + questId + ".cooldown.enabled");
			int cooldown = getConfig().getInt("quests." + questId + ".cooldown.minutes");
			String value = getConfig().getString("quests." + questId + ".value");
			if (questType == QuestType.BUILDINGCERTAIN || questType == QuestType.MININGCERTAIN
					|| questType == QuestType.MOBKILLINGCERTAIN) {
				String[] parts = getConfig().getString("quests." + questId + ".type").split(":");
				value = parts[1] + ":" + value;
			}
			List<String> rewards = getConfig().getStringList("quests." + questId + ".rewards");
			List<String> rewardString = getConfig().getStringList("quests." + questId + ".rewardstring");
			List<String> requirements = new ArrayList<String>();
			if (getConfig().contains("quests." + questId + ".requires")) {
				requirements.add(getConfig().getString("quests." + questId + ".requires"));
			}
			Material materialForItemStack = Material.STONE;
			int dataCode = 0;
			if (getConfig().getString("quests." + questId + ".display.item").contains(":")) {
				String[] materialParts = getConfig().getString("quests." + questId + ".display.item").split(":");
				materialForItemStack = Material.getMaterial(materialParts[0]);
				dataCode = Integer.parseInt(materialParts[1]);
			} else {
				materialForItemStack = Material.getMaterial(getConfig().getString("quests." + questId + ".display.item"));
			}
			ItemStack is = new ItemStack(materialForItemStack, 1, (byte) dataCode);
			ItemMeta ism = is.getItemMeta();
			ism.setDisplayName(ChatColor.translateAlternateColorCodes('&',
					getConfig().getString("quests." + questId + ".display.name")));
			List<String> lore = new ArrayList<String>();
			for (String s : getConfig().getStringList("quests." + questId + ".display.lore")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			ism.setLore(lore);
			is.setItemMeta(ism);
			boolean worldsRestricted = false;
			ArrayList<String> allowedWorlds = new ArrayList<String>();
			if (getConfig().contains("quests." + questId + ".worlds.restricted")) {
				worldsRestricted = getConfig().getBoolean("quests." + questId + ".worlds.restricted");
			}
			if (getConfig().contains("quests." + questId + ".worlds.allowed-worlds")) {
				for (String s : getConfig().getStringList("quests." + questId + ".worlds.allowed-worlds")) {
					allowedWorlds.add(s);
				}
			}
			String customName = "";
			if (getConfig().contains("quests." + questId + ".custom-type")) {
				customName = getConfig().getString("quests." + questId + ".custom-type");
			}
			Quest quest = new Quest(questType, questId, is, redoable, cooldownEnabled, cooldown,
					(ArrayList<String>) rewards, (ArrayList<String>) rewardString, (ArrayList<String>) requirements,
					value, worldsRestricted, allowedWorlds, customName);
			questManager.registerQuest(quest);
			System.out.println("[Quests] Registered quest " + questId + ". Type: " + questType.toString() + (customName.equals("") ? "." : ", custom type: " + customName + "."));
		}
	}

	private void alternateBlockNames() {
		alternateNamesForBlocks.put("GLOWING_REDSTONE_ORE", "REDSTONE_ORE");
	}

	public QuestManager getQuestManager() {
		return questManager;
	}

	public QuestData getQuestData() {
		return questData;
	}

	public static Quests getInstance() {
		return instance;
	}

	public Title getTitle() {
		return title;
	}

	public boolean isTitleEnabled() {
		return titleEnabled;
	}

	public boolean isDebug() {
		return debug;
	}

	public boolean isUskyblockEnabled() {
		return uskyblockEnabled;
	}

	public boolean isAskyblockEnabled() {
		return askyblockEnabled;
	}

	public HashMap<String, String> getAlternateNamesForBlocks() {
		return alternateNamesForBlocks;
	}

	private void cooldownTimer() {
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
					for (String s : data.getConfigurationSection("progress").getKeys(false)) {
						if (questManager.getSelectorMode() == SelectorType.RANDOM) {
							if (questData.getRandomQuestsTimeRemaining(UUID.fromString(s)) <= 0) {
								questData.generateNewRandomQuests(UUID.fromString(s));
								if (Bukkit.getPlayer(UUID.fromString(s)) != null) {
									Bukkit.getPlayer(UUID.fromString(s)).sendMessage(Messages.QUESTS_REFRESHED.getMessage());
								}
							}
						}
					}
				}
			}
		}, 0L, 1200L);
	}

	private void fullStartUp() {
		BukkitScheduler cooldownTimer = getServer().getScheduler();
		cooldownTimer.scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				if (Bukkit.getPluginManager().isPluginEnabled("uSkyBlock")) {
					System.out.println("[Quests] uSkyBlock detected, all USKYBLOCK quests are now functional.");
					uskyblockEnabled = true;
				}
				if (Bukkit.getPluginManager().isPluginEnabled("ASkyBlock")) {
					System.out.println("[Quests] ASkyBlock detected, all ASKYBLOCK quests are now functional.");
					askyblockEnabled = true;
				}
				if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
					ExternalPlaceholders.register();
					System.out.println("[Quests] MVdWPlaceholderAPI detected, all external placeholders are now functional.");
				}
			}

		}, 1L);
	}

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
		} else if (version.equals("v1_12_R1")) {
			title = new Title_v1_12_R1();
			success = true;
		}
		return success;
	}

}