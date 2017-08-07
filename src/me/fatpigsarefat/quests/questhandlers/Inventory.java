package me.fatpigsarefat.quests.questhandlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.utils.Quest;

public class Inventory extends BukkitRunnable {

	private final Quests plugin;

	public Inventory(Quests plugin) {
		this.plugin = plugin;
	}

	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			File d = new File(plugin.getDataFolder() + File.separator + "data.yml");
			YamlConfiguration data = YamlConfiguration.loadConfiguration((File) d);

			if (!data.contains("progress." + player.getUniqueId())) {
				continue;
			}

			Set<String> keys = plugin.getConfig().getConfigurationSection("quests").getKeys(false);
			ArrayList<String> inventoryQuests = new ArrayList<String>();
			List<String> questsStarted = data.getStringList("progress." + player.getUniqueId() + ".quests-started");
			ArrayList<String> questsToComplete = new ArrayList<String>();

			for (String s : keys) {
				String rootPath = "quests." + s + ".";

				if (plugin.getConfig().getString(rootPath + "type").equalsIgnoreCase("INVENTORY")) {
					inventoryQuests.add(s);
				}
			}

			if (inventoryQuests.isEmpty()) {
				continue;
			}
			
			if (Quests.getInstance().getQuestManager().getBlacklistedWorlds().contains(player.getWorld().getName())) {
				continue;
			}

			// Here onwards is a rushed sloppy mess of code stitched together
			// Don't judge me, I overthink things.
			for (String s : inventoryQuests) {
				Quest quest = Quests.getInstance().getQuestManager().getQuestById(s);
				if (quest.isWorldsRestriced()) {
					if (!quest.getAllowedWorlds().contains(player.getWorld().getName())) {
						continue;
					}
				}
				if (questsStarted.contains(s)) {
					String rootPath = "quests." + s + ".";

					String value = plugin.getConfig().getString(rootPath + "value");
					value = value.replace("[", "");
					value = value.replace("]", "");
					ArrayList<ItemStack> requiredMaterials = new ArrayList<ItemStack>();
					ArrayList<ItemStack> requiredMaterials2 = new ArrayList<ItemStack>();
					if (value.contains(", ")) {
						String[] parts = value.split(", ");
						boolean brokenConfig = false;
						for (int i = 0; i < parts.length; i++) {
							int amount = 1;
							String material = parts[i];
							if (parts[i].contains(":")) {
								String[] parts2 = parts[i].split(":");
								amount = Integer.parseInt(parts2[1]);
								material = parts2[0];
							}
							boolean isIdNotString = false;
							try {
								Integer.parseInt(material);
								isIdNotString = true;
							} catch (NumberFormatException ex) {
								isIdNotString = false;
							}
							if (!isIdNotString) {
								if (Material.getMaterial(material) == null) {
									brokenConfig = true;
									continue;
								}
							} else {
								if (Material.getMaterial(Integer.parseInt(material)) == null) {
									brokenConfig = true;
									continue;
								}
							}
							ItemStack is = new ItemStack(isIdNotString ? Material.getMaterial(Integer.parseInt(material)) : Material.getMaterial(material), amount);
							requiredMaterials2.add(is);
							requiredMaterials.add(is);
						}
						if (brokenConfig) {
							continue;
						}
					} else {
						int amount = 1;
						String material = value;
						if (value.contains(":")) {
							String[] parts2 = value.split(":");
							amount = Integer.parseInt(parts2[1]);
							material = parts2[0];
						}
						if (Material.getMaterial(material) == null) {
							continue;
						}
						ItemStack is = new ItemStack(Material.getMaterial(material), amount);
						requiredMaterials.add(is);
						requiredMaterials2.add(is);
					}
					HashMap<Material, Integer> amountThreshold = new HashMap<Material, Integer>();
					for (ItemStack is : requiredMaterials) {
						amountThreshold.put(is.getType(), is.getAmount());
					}
					boolean overwrite = false;
					int take = 0;
					while (take <= 1) {
						int slot = 0;
						for (ItemStack is : player.getInventory().getContents()) {
							if (is == null) {
								continue;
							}
							ItemStack is2 = is.clone();
							boolean isRequiredMaterial = false;
							for (ItemStack isrm : requiredMaterials) {
								if (isrm.getType() == is.getType()) {
									isRequiredMaterial = true;
								}
							}
							if (isRequiredMaterial) {
								if (!amountThreshold.containsKey(is.getType())) {
									continue;
								}
								amountThreshold.put(is.getType(), amountThreshold.get(is.getType()) - is.getAmount());
								if (amountThreshold.get(is.getType()) <= 0) {
									if (overwrite) {
										player.getInventory().setItem(slot, null);
										is2.setAmount(Math.abs(amountThreshold.get(is2.getType())));
										player.getInventory().setItem(slot, is2);
									}
									amountThreshold.remove(is2.getType());
								} else {
									if (overwrite) {
										player.getInventory().setItem(slot, null);
									}
								}
							}
							slot++;
						}
						if (overwrite) {
							questsToComplete.add(s);
							break;
						} else if (amountThreshold.isEmpty()) {
							overwrite = true;
							for (ItemStack is : requiredMaterials) {
								amountThreshold.put(is.getType(), is.getAmount());
							}
						} else {
							break;
						}
						take++;
					}
					if (!overwrite) {
					}
				}
			}

			for (String s : questsToComplete) {
				Quests.getInstance().getQuestData().completeQuest(Quests.getInstance().getQuestManager().getQuestById(s), player.getUniqueId());
			}
		}
	}
}