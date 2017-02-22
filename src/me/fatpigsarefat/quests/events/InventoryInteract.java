package me.fatpigsarefat.quests.events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.fatpigsarefat.quests.Main;
import net.md_5.bungee.api.ChatColor;

public class InventoryInteract implements Listener {

	private final Main plugin;

	public InventoryInteract(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler
	public void playerInteractEvent(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		File d = new File(plugin.getDataFolder() + File.separator + "data.yml");
		YamlConfiguration data = YamlConfiguration.loadConfiguration((File) d);

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

			if (event.getInventory().getName().equals(ChatColor.BLUE + "Quest Builder - Type")) {

			}
		}
	}
}