package me.fatpigsarefat.quests.events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.fatpigsarefat.quests.Main;
import me.fatpigsarefat.quests.Reward;
import net.md_5.bungee.api.ChatColor;

public class BlockPlace implements Listener {

	private final Main plugin;

	public BlockPlace(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		File d = new File(plugin.getDataFolder() + File.separator + "data.yml");
		YamlConfiguration data = YamlConfiguration.loadConfiguration((File) d);

		if (!data.contains("progress." + player.getUniqueId())) {
			return;
		}

		Set<String> keys = plugin.getConfig().getConfigurationSection("quests").getKeys(false);
		ArrayList<String> buildingQuests = new ArrayList<String>();
		ArrayList<String> buildingCertainQuests = new ArrayList<String>();
		List<String> questsStarted = data.getStringList("progress." + player.getUniqueId() + ".quests-started");
		ArrayList<String> questsToAddValue = new ArrayList<String>();

		for (String s : keys) {
			String rootPath = "quests." + s + ".";

			if (plugin.getConfig().getString(rootPath + "type").equalsIgnoreCase("BUILDING")) {
				buildingQuests.add(s);
			} else if (plugin.getConfig().getString(rootPath + "type").contains("BUILDINGCERTAIN")) {
				buildingCertainQuests.add(s);
			}
		}

		if (buildingQuests.isEmpty() && buildingCertainQuests.isEmpty())
			return;

		for (String s : buildingQuests) {
			if (questsStarted.contains(s)) {
				questsToAddValue.add(s);
			}
		}

		for (String s : buildingCertainQuests) {
			if (questsStarted.contains(s)) {
				String rootPath = "quests." + s + ".";

				String value = plugin.getConfig().getString(rootPath + "type");
				String[] parts = value.split(":");
				String st = parts[1];
				if (event.getBlock().getType().equals(Material.getMaterial(Integer.parseInt(st)))) {
					questsToAddValue.add(s);
				}
			}
		}

		if (questsToAddValue.isEmpty())
			return;

		for (String s : questsToAddValue) {
			int value = data.getInt("progress." + player.getUniqueId() + ".quests-progress." + s + ".value");
			value++;
			if (value >= plugin.getConfig().getInt("quests." + s + ".value")) {
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
			data.set("progress." + player.getUniqueId() + ".quests-progress." + s + ".value", value);
			try {
				data.save(d);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
