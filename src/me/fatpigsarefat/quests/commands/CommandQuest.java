package me.fatpigsarefat.quests.commands;

import java.io.File;
import java.io.IOException;
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
			File questlayout = new File(Main.instance.getDataFolder() + File.separator + "questlayout.yml");
			YamlConfiguration questlayoutdata = null;
			if (!questlayout.exists()) {
				try {
					player.sendMessage(ChatColor.GREEN + "Creating new file questlayout.yml...");
					questlayout.createNewFile();
				} catch (IOException e) {
					player.sendMessage(ChatColor.RED + "Failed. See console for error details.");
					e.printStackTrace();
					return true;
				}
			}
			if (questlayout.exists()) {
				questlayoutdata = YamlConfiguration.loadConfiguration((File) questlayout);
				if (!questlayoutdata.contains("active")) {
					questlayoutdata.set("active", false);
					try {
						questlayoutdata.save(questlayout);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			Inventory inv = Bukkit.createInventory(null, 54, ChatColor.BLUE + "Quests");

			if (questlayout.exists() && questlayoutdata.getBoolean("active")) {
				if (questlayoutdata.contains("slot")) {
					for (String s : questlayoutdata.getConfigurationSection("slot").getKeys(false)) {
						if (s.startsWith("CUSTOMITEMSTACK")) {
							int slotId = questlayoutdata.getInt("slot." + s);
							ItemStack is = questlayoutdata.getItemStack("itemstack." + slotId);
							inv.setItem(slotId, is);
						}
					}
				}
			}
			Set<String> keys = plugin.getConfig().getConfigurationSection("quests").getKeys(false);
			plugin.reloadConfig();
			int slot = 0;
			for (String s : keys) {
				if (s.contains("CUSTOMITEMSTACK")) {
					player.sendMessage(ChatColor.RED + "There is an error with the configuration.");
					player.sendMessage(ChatColor.WHITE + "Details:");
					player.sendMessage("Problematic quest: " + s);
					player.sendMessage("Error details: reserved word");
					player.sendMessage("Additional information: n/a");
					player.sendMessage(ChatColor.BOLD
							+ "Problem: CUSTOMITEMSTACK is a reserved word for the Quest GUI designer and therefore cannot be set as a quest identifier.");
					return true;
				}
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
						if (questlayoutdata.contains("slot." + s) && questlayoutdata.getBoolean("active")) {
							inv.setItem(questlayoutdata.getInt("slot." + s), notRedoable);
						} else {
							inv.setItem(slot, notRedoable);
						}
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
							if (questlayoutdata.contains("slot." + s) && questlayoutdata.getBoolean("active")) {
								inv.setItem(questlayoutdata.getInt("slot." + s), notCooldowna);
							} else {
								inv.setItem(slot, notCooldowna);
							}
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
						if (questlayoutdata.contains("slot." + s) && questlayoutdata.getBoolean("active")) {
							inv.setItem(questlayoutdata.getInt("slot." + s), notCompleted);
						} else {
							inv.setItem(slot, notCompleted);
						}
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
					try {
						is = new ItemStack(Material.getMaterial(materialName), 1, (byte) datavalue);
					} catch (Exception e) {
						player.sendMessage(ChatColor.RED + "There is an error with the configuration.");
						player.sendMessage(ChatColor.WHITE + "Details:");
						player.sendMessage("Problematic quest: " + s);
						player.sendMessage("Error details: " + e.getMessage());
						player.sendMessage(
								"Attempted line: is = new ItemStack(Material.getMaterial(materialName), 1, (byte) datavalue)");
						player.sendMessage("Additional information: ");
						player.sendMessage("materialName: " + materialName + ", " + "datavalue: " + datavalue);
						player.sendMessage(ChatColor.BOLD + "Problem: failed to get the material type. Does it exist?");
						return true;
					}
				} else {
					try {
						is = new ItemStack(Material.getMaterial(materialName));
					} catch (NullPointerException e) {
						player.sendMessage(ChatColor.RED + "There is an error with the configuration.");
						player.sendMessage(ChatColor.WHITE + "Details:");
						player.sendMessage("Problematic quest: " + s);
						player.sendMessage("Error details: " + e.getMessage());
						player.sendMessage("Attempted line: is = new ItemStack(Material.getMaterial(materialName))");
						player.sendMessage("Additional information: ");
						player.sendMessage("materialName: " + materialName);
						player.sendMessage(ChatColor.BOLD + "Problem: failed to get the material type. Does it exist?");
						return true;
					}
				}
				if (is == null) {
					player.sendMessage(ChatColor.RED + "There is an error with the configuration.");
					player.sendMessage(ChatColor.WHITE + "Details:");
					player.sendMessage("Problematic quest: " + s);
					player.sendMessage("Error details: failed display (itemstack) null check");
					player.sendMessage("Attempted line: if (is == null)");
					player.sendMessage("Additional information: n/a");
					player.sendMessage(ChatColor.BOLD + "Problem: display itemstack is null");
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

				if (questlayoutdata.contains("slot." + s) && questlayoutdata.getBoolean("active")) {
					inv.setItem(questlayoutdata.getInt("slot." + s), is);
				} else {
					inv.setItem(slot, is);
				}
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
