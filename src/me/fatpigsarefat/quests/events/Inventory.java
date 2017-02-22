package me.fatpigsarefat.quests.events;

import java.io.File;
import java.io.IOException;
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

import me.fatpigsarefat.quests.Main;
import me.fatpigsarefat.quests.Reward;
import org.bukkit.ChatColor;

public class Inventory extends BukkitRunnable {

	private final Main plugin;

	public Inventory(Main plugin) {
		this.plugin = plugin;
	}

	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			File d = new File(plugin.getDataFolder() + File.separator + "data.yml");
			YamlConfiguration data = YamlConfiguration.loadConfiguration((File) d);

			if (!data.contains("progress." + player.getUniqueId())) {
				return;
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

			if (inventoryQuests.isEmpty())
				return;

			// Here onwards is a rushed sloppy mess of code stitched together
			// Don't judge me, I overthink things.
			for (String s : inventoryQuests) {
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
							if (Material.getMaterial(material) == null) {
								brokenConfig = true;
								continue;
							}
							ItemStack is = new ItemStack(Material.getMaterial(material), amount);
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
					// Look, I know there probably is an easier way of doing
					// this, but if you're here just to bash on me for not doing
					// it 'correctly' then don't even bother. I know.
					// ..
					// nvm i give up.
					// inventory quests can come next update.
					while (take <= 1) {
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
										player.getInventory().remove(is);
										is2.setAmount(0 - amountThreshold.get(is2.getType()));
										if (is2.getAmount() > 0) {
											player.getInventory().addItem(is2);
										}
									}
									amountThreshold.remove(is2.getType());
								} else {
									if (overwrite) {
										player.getInventory().remove(is.getType());
									}
								}
							}
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
						// Just in case it doesn't break out of the loop, we
						// don't want any infinite loops do we now?
						take++;
					}
				}
			}

			for (String s : questsToComplete) {
				String titleMessage = "";
				String titleSubMessage = "";
				player.sendMessage(ChatColor.GREEN + "Successfully completed " + ChatColor.translateAlternateColorCodes(
						'&', plugin.getConfig().getString("quests." + s + ".display.name")));
				if (plugin.getConfig().getString("title.enabled").equals("true")) {
					if (!Main.instance.titleEnabled) {
						return;
					}
					titleMessage = ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("title.mainmessage"));
					titleMessage = titleMessage.replace("%quest%", ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("quests." + s + ".display.name")));
					titleSubMessage = ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("title.submessage"));
					titleSubMessage = titleSubMessage.replace("%quest%", ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("quests." + s + ".display.name")));
					plugin.title.sendTitle(player, titleMessage, titleSubMessage);
				}
				int i = 0;
				for (String quest : questsStarted) {
					if (quest.equals(s)) {
						questsStarted.remove(i);
						break;
					}
					i++;
				}
				new Reward(plugin, player, s);
				data.set("progress." + player.getUniqueId() + ".quests-progress." + s, null);
				data.set("progress." + player.getUniqueId() + ".quests-started", questsStarted);
				List<String> questsCompleted = new ArrayList<>();
				if (data.contains("progress." + player.getUniqueId() + ".quests-completed")) {
					questsCompleted = data.getStringList("progress." + player.getUniqueId() + ".quests-completed");
				}

				int cooldownTime = 0;
				if (plugin.getConfig().contains("quests." + s + ".cooldown")) {
					if (plugin.getConfig().getBoolean("quests." + s + ".cooldown.enabled")) {
						cooldownTime = plugin.getConfig().getInt("quests." + s + ".cooldown.minutes");
					}
				}

				if (!(cooldownTime == 0)) {
					data.set("progress." + player.getUniqueId() + ".quests-cooldown." + s, cooldownTime);
				}
				questsCompleted.add(s);

				data.set("progress." + player.getUniqueId() + ".quests-completed", questsCompleted);
				try {
					data.save(d);
				} catch (IOException e) {
					e.printStackTrace();
				}
				continue;

			}
		}
	}
}

//
// hello person
//
// just like to say how much i actually hate coding but oh well
//
// just gotta keep updating my plugins cuz people keep pestering me to do so and
// to fix the bugs. yay me.
//
//