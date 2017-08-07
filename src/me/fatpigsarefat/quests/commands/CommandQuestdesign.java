package me.fatpigsarefat.quests.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.fatpigsarefat.quests.Quests;

public class CommandQuestdesign implements CommandExecutor {

	public static Player inEditor = null;
	public static ArrayList<Player> confirm = new ArrayList<Player>();
	static HashMap<String, ItemStack> mappedItemStacks = new HashMap<String, ItemStack>();
	static HashMap<String, Integer> slots = new HashMap<String, Integer>();
	static HashMap<ItemStack, Integer> customitemstacks = new HashMap<ItemStack, Integer>();
	static ArrayList<ItemStack> slotless = new ArrayList<ItemStack>();
	static File d = null;
	static YamlConfiguration data = null;

	@SuppressWarnings({ "rawtypes" })
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getLabel().equalsIgnoreCase("questguidesigner") && sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission("quest.admin")) {
				p.sendMessage("No permission.");
				return true;
			}
			if (!confirm.contains(p)) {
				Inventory inv = Bukkit.createInventory(null, 27, ChatColor.RED + "Quests - Are you sure?");

				ItemStack confirmIs = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
				ItemMeta confirmIsM = confirmIs.getItemMeta();
				confirmIsM.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Confirm");
				confirmIs.setItemMeta(confirmIsM);

				ItemStack paperIs = new ItemStack(Material.PAPER);
				ItemMeta paperIsM = paperIs.getItemMeta();
				paperIsM.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + "Are you sure?");
				List<String> l = new ArrayList<String>();
				l.add(ChatColor.GRAY + "A part of your inventory will be cleared if");
				l.add(ChatColor.GRAY + "you enter the GUI designer!");
				l.add(ChatColor.GRAY + "");
				l.add(ChatColor.RED + "Note: The Quest GUI Designer can be buggy");
				l.add(ChatColor.RED + "and some times items wont be properly");
				l.add(ChatColor.RED + "assigned a slot.");
				l.add(ChatColor.RED + "");
				l.add(ChatColor.RED + "It is recommended you close and re-open");
				l.add(ChatColor.RED + "the GUI designer while editing frequently");
				l.add(ChatColor.RED + "to check for any unassigned quests.");
				paperIsM.setLore(l);
				paperIs.setItemMeta(paperIsM);

				ItemStack cancelIs = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
				ItemMeta cancelIsM = cancelIs.getItemMeta();
				cancelIsM.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + "Cancel");
				cancelIs.setItemMeta(cancelIsM);

				inv.setItem(10, cancelIs);
				inv.setItem(11, cancelIs);
				inv.setItem(13, paperIs);
				inv.setItem(15, confirmIs);
				inv.setItem(16, confirmIs);

				p.openInventory(inv);
			} else if (inEditor != null) {
				p.sendMessage(ChatColor.RED + inEditor.getName()
						+ " is already using the Quest GUI editor. Try again later.");
			} else {
				CommandQuestdesign.confirm.remove(p);
				inEditor = p;
				d = new File(Quests.getInstance().getDataFolder() + File.separator + "questlayout.yml");
				if (!d.exists()) {
					try {
						p.sendMessage(ChatColor.GREEN + "Creating new file questlayout.yml...");
						d.createNewFile();
					} catch (IOException e) {
						p.sendMessage(ChatColor.RED + "Failed. See console for error details.");
						e.printStackTrace();
						return true;
					}
				} else {
					p.sendMessage(ChatColor.GREEN + "Loading configuraton from questlayout.yml...");
				}
				data = YamlConfiguration.loadConfiguration((File) d);
				if (!data.contains("active")) {
					data.set("active", false);
					try {
						data.save(d);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				Set<String> keys = Quests.getInstance().getConfig().getConfigurationSection("quests").getKeys(false);
				for (String s : keys) {
					if (s.contains("CUSTOMITEMSTACK")) {
						p.sendMessage(ChatColor.RED + "There is an error with the configuration.");
						p.sendMessage(ChatColor.WHITE + "Details:");
						p.sendMessage("Problematic quest: " + s);
						p.sendMessage("Error details: reserved word");
						p.sendMessage("Additional information: n/a");
						p.sendMessage(ChatColor.BOLD
								+ "Problem: CUSTOMITEMSTACK is a reserved word for the Quest GUI designer and therefore cannot be set as a quest identifier.");
						return true;
					}
					String rootPath = "quests." + s + ".display.";

					String materialName = Quests.getInstance().getConfig().getString(rootPath + "item").toUpperCase();
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
							p.sendMessage(ChatColor.RED + "There is an error with the configuration.");
							p.sendMessage(ChatColor.WHITE + "Details:");
							p.sendMessage("Problematic quest: " + s);
							p.sendMessage("Error details: " + e.getMessage());
							p.sendMessage(
									"Attempted line: is = new ItemStack(Material.getMaterial(materialName), 1, (byte) datavalue)");
							p.sendMessage("Additional information: ");
							p.sendMessage("materialName: " + materialName + ", " + "datavalue: " + datavalue);
							p.sendMessage(ChatColor.BOLD + "Problem: failed to get the material type. Does it exist?");
							return true;
						}
					} else {
						try {
							is = new ItemStack(Material.getMaterial(materialName));
						} catch (NullPointerException e) {
							p.sendMessage(ChatColor.RED + "There is an error with the configuration.");
							p.sendMessage(ChatColor.WHITE + "Details:");
							p.sendMessage("Problematic quest: " + s);
							p.sendMessage("Error details: " + e.getMessage());
							p.sendMessage("Attempted line: is = new ItemStack(Material.getMaterial(materialName))");
							p.sendMessage("Additional information: ");
							p.sendMessage("materialName: " + materialName);
							p.sendMessage(ChatColor.BOLD + "Problem: failed to get the material type. Does it exist?");
							return true;
						}
					}
					ItemMeta ism = is.getItemMeta();
					ism.setDisplayName(ChatColor.translateAlternateColorCodes('&',
							Quests.getInstance().getConfig().getString(rootPath + "name")));
					ArrayList<String> lore = new ArrayList<String>();
					lore.add(ChatColor.GRAY + "Quest ID: " + s);
					ism.setLore(lore);
					is.setItemMeta(ism);

					if (hasAssignedSlot(s)) {
						int slot = getAssignedSlot(s);
						if (slots.containsValue(slot)) {
							p.sendMessage(ChatColor.RED + "Duplicate assigned slots for slot " + slot);
							slotless.add(is);
							removeAssignedSlot(s);
							p.sendMessage(ChatColor.RED + s + " has been unassigned.");
						} else {
							slots.put(s, slot);
						}
					} else {
						slotless.add(is);
					}
					mappedItemStacks.put(s, is);
				}
				if (data.contains("slot")) {
					for (String s : data.getConfigurationSection("slot").getKeys(false)) {
						if (s.startsWith("CUSTOMITEMSTACK")) {
							s = s.replace("CUSTOMITEMSTACK", "");
							customitemstacks.put(getAssignedStack(Integer.parseInt(s)), Integer.parseInt(s));
						}
					}
				}

				Inventory inv = Bukkit.createInventory(null, Quests.getInstance().getConfig().getInt("gui.slots"), ChatColor.BLUE + "Quest GUI designer");

				Iterator cit = customitemstacks.entrySet().iterator();
				while (cit.hasNext()) {
					Entry em = (Entry) cit.next();
					ItemStack is = (ItemStack) em.getKey();
					int s = (int) em.getValue();
					inv.setItem(s, is);
				}

				Iterator sit = slots.entrySet().iterator();
				while (sit.hasNext()) {
					Entry em = (Entry) sit.next();
					String st = (String) em.getKey();
					int s = (int) em.getValue();
					ItemStack is = mappedItemStacks.get(st);
					inv.setItem(s, is);
				}

				for (ItemStack is : slotless) {
					inv.addItem(is);
				}
				ItemStack spacer = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);
				ItemMeta spacerm = spacer.getItemMeta();
				spacerm.setDisplayName(" ");
				spacer.setItemMeta(spacerm);

				ItemStack setactive = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
				ItemMeta setactivem = setactive.getItemMeta();
				List<String> setactivel = new ArrayList<String>();
				setactivel.add(ChatColor.GRAY + "Set this customised page as the active");
				setactivel.add(ChatColor.GRAY + "quest selector.");
				setactivem.setLore(setactivel);
				setactivem.setDisplayName(ChatColor.GREEN + "Activate");
				setactive.setItemMeta(setactivem);

				ItemStack sethidden = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
				ItemMeta sethiddenm = sethidden.getItemMeta();
				List<String> sethiddenl = new ArrayList<String>();
				sethiddenl.add(ChatColor.GRAY + "De-activate the customised quest selector");
				sethiddenl.add(ChatColor.GRAY + "and use the default quest selector.");
				sethiddenm.setLore(sethiddenl);
				sethiddenm.setDisplayName(ChatColor.RED + "Deactivate");
				sethidden.setItemMeta(sethiddenm);

				ItemStack reset = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 1);
				ItemMeta resetm = reset.getItemMeta();
				List<String> resetl = new ArrayList<String>();
				resetl.add(ChatColor.GRAY + "Use this button at your own risk!");
				resetm.setLore(resetl);
				resetm.setDisplayName(ChatColor.RED + "Reset");
				reset.setItemMeta(resetm);

				ItemStack information = new ItemStack(Material.PAPER);
				ItemMeta informationm = information.getItemMeta();
				informationm.setDisplayName(ChatColor.BLUE + "Welcome to the Quest GUI designer!");
				List<String> informationl = new ArrayList<String>();
				informationl.add(ChatColor.GRAY + "Anything below the spacer can be added");
				informationl.add(ChatColor.GRAY + "to the GUI, but will not be a clickable");
				informationl.add(ChatColor.GRAY + "quest! (this allows you to create items");
				informationl.add(ChatColor.GRAY + "which can label a group of quests)");
				informationl.add(ChatColor.GRAY + " ");
				informationl.add(ChatColor.GRAY + "Any quests which are removed from the GUI");
				informationl.add(ChatColor.GRAY + "and aren't set a slot manually will be added");
				informationl.add(ChatColor.GRAY + "to the Quest GUI in the next free slot.");
				informationl.add(ChatColor.GRAY + " ");
				informationl.add(ChatColor.GRAY + "See the plugin page for more information");
				informationl.add(ChatColor.GRAY + "and help.");
				informationm.setLore(informationl);
				information.setItemMeta(informationm);

				inEditor.getInventory().setItem(9, reset);
				inEditor.getInventory().setItem(10, spacer);
				inEditor.getInventory().setItem(11, spacer);
				inEditor.getInventory().setItem(12, spacer);
				inEditor.getInventory().setItem(13, information);
				inEditor.getInventory().setItem(14, spacer);
				inEditor.getInventory().setItem(15, spacer);
				inEditor.getInventory().setItem(16, sethidden);
				inEditor.getInventory().setItem(17, setactive);

				inEditor.openInventory(inv);
			}
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public static void finishEditing() {
		Iterator cit = customitemstacks.entrySet().iterator();
		while (cit.hasNext()) {
			Entry em = (Entry) cit.next();
			ItemStack is = (ItemStack) em.getKey();
			int s = (int) em.getValue();
			setAssignedSlot(is, s);
		}
		Iterator sit = slots.entrySet().iterator();
		while (sit.hasNext()) {
			Entry em = (Entry) sit.next();
			String st = (String) em.getKey();
			int s = (int) em.getValue();
			setAssignedSlot(st, s);
		}
		inEditor = null;
		mappedItemStacks.clear();
		customitemstacks.clear();
		slots.clear();
		slotless.clear();
		data = null;
		d = null;
	}

	public static boolean hasAssignedSlot(String s) {
		if (data.contains("slot." + s)) {
			return true;
		}
		return false;
	}

	public static int getAssignedSlot(String s) {
		return (data.getInt("slot." + s));
	}

	public static int getAssignedSlot(ItemStack is) {
		if (data.contains("itemstack")) {
			for (String s : data.getConfigurationSection("itemstack").getKeys(false)) {
				if (data.getItemStack("itemstack." + s).equals(is)) {
					return Integer.parseInt(s);
				}
			}
		}
		return -1;
	}

	public static ItemStack getAssignedStack(int i) {
		if (data.contains("itemstack." + i)) {
			return data.getItemStack("itemstack." + i);
		}
		return null;
	}

	public static void setAssignedSlot(String s, int slot) {
		data.set("slot." + s, slot);
		slots.put(s, slot);
		try {
			data.save(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	public static void setAssignedSlot(ItemStack is, int slot) {
		data.set("slot.CUSTOMITEMSTACK" + slot, slot);
		customitemstacks.put(is, slot);
		data.set("itemstack." + slot, is);
		try {
			data.save(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	public static void removeAssignedSlot(String s) {
		data.set("slot." + s, null);
		slots.remove(s);
		try {
			data.save(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void removeAssignedSlot(int i) {
		if (data.contains("slot")) {
			for (String s : data.getConfigurationSection("slot").getKeys(false)) {
				if (data.getInt("slot." + s) == i) {
					data.set("slot." + s, null);
					if (slots.containsKey(s)) {
						slots.remove(s);
					}
					if (customitemstacks.containsValue(i)) {
						customitemstacks.remove(getAssignedStack(i));
					}
					try {
						data.save(d);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void resetEverything() {
		Set<String> keys = Quests.getInstance().getConfig().getConfigurationSection("quests").getKeys(false);
		for (String s : keys) {
			if (slots.containsKey(s))
				slots.remove(s);
			removeAssignedSlot(s);
		}
	}

	public static void activate() {
		data.set("active", true);
		try {
			data.save(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deactivate() {
		data.set("active", false);
		try {
			data.save(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}