package me.fatpigsarefat.quests.events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.fatpigsarefat.quests.Main;
import me.fatpigsarefat.quests.commands.CommandQuestdesign;
import net.md_5.bungee.api.ChatColor;

public class InventoryInteract implements Listener {

	private final Main plugin;

	public InventoryInteract(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler
	public void playerCloseInventory(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (event.getInventory().getName().equals(ChatColor.BLUE + "Quest GUI designer")) {
			player.getInventory().setItem(9, null);
			player.getInventory().setItem(10, null);
			player.getInventory().setItem(11, null);
			player.getInventory().setItem(12, null);
			player.getInventory().setItem(13, null);
			player.getInventory().setItem(14, null);
			player.getInventory().setItem(15, null);
			player.getInventory().setItem(16, null);
			player.getInventory().setItem(17, null);
			CommandQuestdesign.finishEditing();
		}
	}

	@EventHandler
	public void playerInteractEvent(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		File d = new File(plugin.getDataFolder() + File.separator + "data.yml");
		YamlConfiguration data = YamlConfiguration.loadConfiguration((File) d);

		if (event.getInventory().getName().equals(ChatColor.RED + "Quests - Are you sure?")) {
			event.setCancelled(true);
			if (event.getSlot() == 10 || event.getSlot() == 11) {
				player.closeInventory();
			} else if (event.getSlot() == 15 || event.getSlot() == 16) {
				CommandQuestdesign.confirm.add(player);
				player.closeInventory();
				Bukkit.dispatchCommand(player, "qgui");
			}
		}
		if (player.getOpenInventory().getTopInventory().getName().equals(ChatColor.BLUE + "Quest GUI designer")) {
			if (CommandQuestdesign.inEditor.equals(player)) {
				if (event.getRawSlot() >= event.getView().getTopInventory().getSize()) {
					if (event.getSlot() >= 9 && event.getSlot() <= 17) {
						event.setCancelled(true);
						if (event.getSlot() == 17) {
							CommandQuestdesign.activate();
							player.sendMessage(ChatColor.GREEN + "GUI is now set as the active Quest GUI.");
						} else if (event.getSlot() == 16) {
							CommandQuestdesign.deactivate();
							player.sendMessage(ChatColor.RED + "GUI is no longer set as the active Quest GUI.");
						} else if (event.getSlot() == 9) {
							CommandQuestdesign.resetEverything();
							player.closeInventory();
						}
					}
				} else {
					if (event.getCursor().getType() == Material.AIR
							&& event.getCurrentItem().getType() == Material.AIR) {

					} else if (event.getCursor().getType() == Material.AIR) {
						boolean ciIsQuest = false;
						ItemStack ci = event.getCurrentItem().clone();
						if (ci.getItemMeta().hasLore()) {
							for (String s : ci.getItemMeta().getLore()) {
								s = ChatColor.stripColor(s);
								if (s.startsWith("Quest ID: ")) {
									s = s.replace("Quest ID: ", "");
									if (CommandQuestdesign.hasAssignedSlot(s)) {
										player.sendMessage(ChatColor.RED + s + " unassigned");
										CommandQuestdesign.removeAssignedSlot(s);
									}
									ciIsQuest = true;
								}
							}
						}
						if (!ciIsQuest) {
							if (CommandQuestdesign.getAssignedSlot(ci) != -1) {
								player.sendMessage(ChatColor.RED + "CUSTOMITEMSTACK" + event.getSlot() + " unassigned");
								CommandQuestdesign.removeAssignedSlot(event.getSlot());
							}
						}
					} else if (event.getCurrentItem().getType() == Material.AIR) {
						boolean cuIsQuest = false;
						ItemStack cu = event.getCursor().clone();
						if (cu.getItemMeta().hasLore()) {
							for (String s : cu.getItemMeta().getLore()) {
								s = ChatColor.stripColor(s);
								if (s.startsWith("Quest ID: ")) {
									s = s.replace("Quest ID: ", "");
									player.sendMessage(ChatColor.GREEN + s + " assigned to " + event.getSlot());
									CommandQuestdesign.setAssignedSlot(s, event.getSlot());
									cuIsQuest = true;
								}
							}
						}
						if (!cuIsQuest) {
							player.sendMessage(ChatColor.GREEN + "CUSTOMITEMSTACK" + event.getSlot() + " assigned to " + event.getSlot());
							CommandQuestdesign.setAssignedSlot(cu, event.getSlot());
						}
					} else {
						boolean cuIsQuest = false;
						ItemStack cu = event.getCursor().clone();
						if (cu.getItemMeta().hasLore()) {
							for (String s : cu.getItemMeta().getLore()) {
								s = ChatColor.stripColor(s);
								if (s.startsWith("Quest ID: ")) {
									s = s.replace("Quest ID: ", "");
									player.sendMessage(ChatColor.GREEN + s + " assigned to " + event.getSlot());
									CommandQuestdesign.setAssignedSlot(s, event.getSlot());
									cuIsQuest = true;
								}
							}
						}
						if (!cuIsQuest) {
							player.sendMessage(ChatColor.GREEN + "CUSTOMITEMSTACK" + event.getSlot() + " assigned to " + event.getSlot());
							CommandQuestdesign.setAssignedSlot(cu, event.getSlot());
						}
						boolean ciIsQuest = false;
						ItemStack ci = event.getCurrentItem().clone();
						if (ci.getItemMeta().hasLore()) {
							for (String s : ci.getItemMeta().getLore()) {
								s = ChatColor.stripColor(s);
								if (s.startsWith("Quest ID: ")) {
									s = s.replace("Quest ID: ", "");
									if (CommandQuestdesign.hasAssignedSlot(s)) {
										player.sendMessage(ChatColor.RED + s + " unassigned");
										CommandQuestdesign.removeAssignedSlot(s);
									}
									ciIsQuest = true;
								}
							}
						}
						if (!ciIsQuest) {
							if (CommandQuestdesign.getAssignedSlot(ci) > -1) {
								player.sendMessage(ChatColor.RED + "CUSTOMITEMSTACK" + event.getSlot() + " unassigned");
								CommandQuestdesign.removeAssignedSlot(event.getSlot());
							}
						}
					}
				}
			}
		}

		if (event.getInventory().getName().equals(ChatColor.BLUE + "Quests")) {
			if (event.getInventory().getName().equalsIgnoreCase(ChatColor.BLUE + "Quests")) {
				event.setCancelled(true);
				if ((event.getCurrentItem() == null) || (event.getCurrentItem().getType() == Material.AIR)
						|| (!event.getCurrentItem().hasItemMeta())) {
					return;
				}
				event.setCancelled(true);

				ItemStack itemClicked = event.getCurrentItem();

				ItemStack notCompleted = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
				ItemMeta notCompletedM = notCompleted.getItemMeta();
				notCompletedM.setDisplayName(ChatColor.RED + "Locked");
				notCompleted.setItemMeta(notCompletedM);

				ItemStack notRedoable = new ItemStack(Material.COMPASS);
				ItemMeta notRedoableM = notRedoable.getItemMeta();
				notRedoableM.setDisplayName(ChatColor.RED + "Completed");
				notRedoable.setItemMeta(notRedoableM);

				if (itemClicked.equals(notCompleted) || itemClicked.equals(notRedoable)) {
					return;
				}
				for (String s : plugin.getConfig().getConfigurationSection("quests").getKeys(false)) {
					String name = ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("quests." + s + ".display.name"));
					if (itemClicked.getItemMeta().getDisplayName().equals(name)) {
						data.set("progress." + player.getUniqueId() + ".name", player.getName());
						List<String> questsStarted = new ArrayList<String>();
						if (data.contains("progress." + player.getUniqueId() + ".quests-started")) {
							questsStarted = data.getStringList("progress." + player.getUniqueId() + ".quests-started");
						}
						if (questsStarted.contains(s)) {
							player.sendMessage(ChatColor.RED + "Quest already started!");
							return;
						}
						questsStarted.add(s);
						data.set("progress." + player.getUniqueId() + ".quests-started", questsStarted);
						try {
							data.save(d);
						} catch (IOException e) {
							e.printStackTrace();
						}
						player.sendMessage(ChatColor.GREEN + "Started quest: " + name);
						player.closeInventory();
						break;
					}
				}
			}
		}

		if (event.getInventory().getName().equals(ChatColor.BLUE + "Quest Builder - Type")) {

		}
	}
}