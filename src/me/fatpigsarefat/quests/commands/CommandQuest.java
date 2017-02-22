package me.fatpigsarefat.quests.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.fatpigsarefat.quests.Main;
import net.md_5.bungee.api.ChatColor;

public class CommandQuest implements CommandExecutor {

	private final Main plugin;

	public CommandQuest(Main plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getLabel().equalsIgnoreCase("quest") && sender instanceof Player) {
			Player player = (Player) sender;

			if (!player.hasPermission("quest.command")) {
				player.sendMessage(ChatColor.RED + "No permission.");
			}

			File d = new File(plugin.getDataFolder() + File.separator + "data.yml");
			YamlConfiguration data = YamlConfiguration.loadConfiguration((File) d);

			Inventory inv = Bukkit.createInventory(null, 54, ChatColor.BLUE + "Quests");

			Set<String> keys = plugin.getConfig().getConfigurationSection("quests").getKeys(false);
			plugin.reloadConfig();
			int slot = 0;
			for (String s : keys) {
				String rootPath = "quests." + s + ".display.";

				List<String> questsCompleted = new ArrayList<String>();
				if (data.contains("progress." + player.getUniqueId() + ".quests-completed")) {
					questsCompleted = data.getStringList("progress." + player.getUniqueId() + ".quests-completed");
				}
				if (plugin.getConfig().contains("quests." + s + ".redoable")) {
					Material mat = null;
					int id = 0;
					ItemStack notRedoable;
					if (plugin.getConfig().getString("gui.completed.item").contains(":")) {
						String[] st = plugin.getConfig().getString("gui.completed.item").split(":");
						mat = Material.getMaterial(st[0]);
						id = Integer.parseInt(st[1]);
						notRedoable = new ItemStack(mat, 1, (byte) id);
					} else {
						notRedoable = new ItemStack(mat, 1);
					}
					ItemMeta notRedoableM = notRedoable.getItemMeta();
					notRedoableM.setDisplayName(ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("gui.completed.name")));
					List<String> lore = new ArrayList<String>();
					if (plugin.getConfig().contains("gui.completed.lore")) {
						for (String str : plugin.getConfig().getStringList("gui.completed.lore")) {
							lore.add(ChatColor.translateAlternateColorCodes('&', str));
						}
					}
					notRedoableM.setLore(lore);
					notRedoable.setItemMeta(notRedoableM);
					boolean redoable = plugin.getConfig().getBoolean("quests." + s + ".redoable");
					boolean questFound = false;
					for (String s1 : questsCompleted) {
						if (s1.equals(s)) {
							questFound = true;
							break;
						}
					}
					if (questFound && !redoable) {
						inv.setItem(slot, notRedoable);
						slot++;
						continue;
					}
				}
				if (plugin.getConfig().contains("quests." + s + ".cooldown")) {
					boolean cooldown = plugin.getConfig().getBoolean("quests." + s + ".redoable");
					if (cooldown) {
						if (data.contains("progress." + player.getUniqueId() + ".quests-cooldown." + s)) {
							Material mat = null;
							int id = 0;
							ItemStack notCooldowna;
							if (plugin.getConfig().getString("gui.cooldown.item").contains(":")) {
								String[] st = plugin.getConfig().getString("gui.cooldown.item").split(":");
								mat = Material.getMaterial(st[0]);
								id = Integer.parseInt(st[1]);
								notCooldowna = new ItemStack(mat, 1, (byte) id);
							} else {
								notCooldowna = new ItemStack(mat, 1);
							}
							ItemMeta notCooldownaM = notCooldowna.getItemMeta();
							notCooldownaM.setDisplayName(ChatColor.translateAlternateColorCodes('&',
									plugin.getConfig().getString("gui.cooldown.name")));
							List<String> lore = new ArrayList<String>();
							if (plugin.getConfig().contains("gui.cooldown.lore")) {
								for (String str : plugin.getConfig().getStringList("gui.cooldown.lore")) {
									str = str.replace("%cooldown%", convertToFormat(
											data.getInt("progress." + player.getUniqueId() + ".quests-cooldown." + s)));
									lore.add(ChatColor.translateAlternateColorCodes('&', str));
								}
							}
							notCooldownaM.setLore(lore);
							notCooldowna.setItemMeta(notCooldownaM);
							inv.setItem(slot, notCooldowna);
							slot++;
							continue;
						}
					}
				}
				if (plugin.getConfig().contains("quests." + s + ".requires")) {
					Material mat = null;
					int id = 0;
					ItemStack notCompleted;
					if (plugin.getConfig().getString("gui.locked.item").contains(":")) {
						String[] st = plugin.getConfig().getString("gui.locked.item").split(":");
						mat = Material.getMaterial(st[0]);
						id = Integer.parseInt(st[1]);
						notCompleted = new ItemStack(mat, 1, (byte) id);
					} else {
						notCompleted = new ItemStack(mat, 1);
					}
					ItemMeta notCompletedM = notCompleted.getItemMeta();
					notCompletedM.setDisplayName(ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("gui.locked.name")));
					List<String> lore = new ArrayList<String>();
					if (plugin.getConfig().contains("gui.locked.lore")) {
						for (String str : plugin.getConfig().getStringList("gui.locked.lore")) {
							lore.add(ChatColor.translateAlternateColorCodes('&', str));
						}
					}
					notCompletedM.setLore(lore);
					notCompleted.setItemMeta(notCompletedM);
					String requirement = plugin.getConfig().getString("quests." + s + ".requires");
					boolean questFound = false;
					for (String s1 : questsCompleted) {
						if (s1.equals(requirement)) {
							questFound = true;
							break;
						}
					}
					if (!questFound) {
						inv.setItem(slot, notCompleted);
						slot++;
						continue;
					}
				}
				boolean questStarted = false;
				List<String> questsStarted = new ArrayList<String>();
				if (data.contains("progress." + player.getUniqueId() + ".quests-started")) {
					questsStarted = data.getStringList("progress." + player.getUniqueId() + ".quests-started");
				}
				if (questsStarted.contains(s)) {
					questStarted = true;
				}
				
				String materialName = plugin.getConfig().getString(rootPath + "item").toUpperCase();
				boolean datav = false;
				int datavalue = 0;
				if (materialName.contains(":")) {
					datav = true;
					String[] parts = materialName.split(":");
					materialName = parts[0];
					datavalue = Integer.parseInt(parts[1]);
				}
				
				ItemStack is = null;
				if (datav) {
					is = new ItemStack(Material.getMaterial(materialName), 1, (byte) datavalue);
				} else {
					is = new ItemStack(Material.getMaterial(materialName));
				}
				if (is == null) {
					player.sendMessage(ChatColor.RED + "Broken config, path: " + rootPath);
				}
				ItemMeta ism = is.getItemMeta();
				ism.setDisplayName(
						ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(rootPath + "name")));
				ArrayList<String> lore = new ArrayList<String>();
				for (String loreLine : plugin.getConfig().getStringList(rootPath + "lore")) {
					if (questStarted && data.contains("progress." + player.getUniqueId() + ".quests-progress." + s)) {
						loreLine = loreLine.replace("%progress%", data
								.getString("progress." + player.getUniqueId() + ".quests-progress." + s + ".value"));
					} else {
						loreLine = loreLine.replace("%progress%", "0");
					}
					lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
				}
				ism.setLore(lore);
				is.setItemMeta(ism);

				if (questStarted)
					is.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);

				inv.setItem(slot, is);
				slot++;
			}

			player.openInventory(inv);
			player.updateInventory();
			return true;
		}

		if (cmd.getLabel().equalsIgnoreCase("quest") && !(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Sorry, players only!");
			return true;
		}

		return false;
	}

	public String convertToFormat(int m) {
		int hours = m / 60;
		int minutesLeft = m - hours * 60;

		String formattedTime = "";

		if (hours < 10)
			formattedTime = formattedTime + "0";
		formattedTime = formattedTime + hours + "h";

		formattedTime = formattedTime + " ";

		if (minutesLeft < 10)
			formattedTime = formattedTime + "0";
		formattedTime = formattedTime + minutesLeft + "m";

		return formattedTime;
	}

}
