package me.fatpigsarefat.quests.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.fatpigsarefat.quests.Main;
import org.bukkit.ChatColor;

public class CommandQuestcreate implements CommandExecutor {

	// Commented out for my own sanity.
	// private final Main plugin;

	public CommandQuestcreate(Main plugin) {
		// this.plugin = plugin;
	}

	// hush child, this is coming, be patient ;)
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getLabel().equalsIgnoreCase("questcreate") && sender instanceof Player) {
			Player player = (Player) sender;

			Inventory inv = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Quest Builder - Type");

			ItemStack mining = new ItemStack(Material.STONE_PICKAXE);
			ItemMeta miningM = mining.getItemMeta();
			miningM.setDisplayName(ChatColor.BLUE + "Mining");
			mining.setItemMeta(miningM);

			ItemStack building = new ItemStack(Material.GRASS);
			ItemMeta buildingM = building.getItemMeta();
			buildingM.setDisplayName(ChatColor.BLUE + "Building");
			building.setItemMeta(buildingM);

			ItemStack mobkilling = new ItemStack(Material.IRON_CHESTPLATE);
			ItemMeta mobkillingM = mobkilling.getItemMeta();
			mobkillingM.setDisplayName(ChatColor.BLUE + "Mob Killing");
			mobkilling.setItemMeta(mobkillingM);

			ItemStack playerkilling = new ItemStack(Material.IRON_SWORD);
			ItemMeta playerkillingM = playerkilling.getItemMeta();
			playerkillingM.setDisplayName(ChatColor.BLUE + "Player Killing");
			playerkilling.setItemMeta(playerkillingM);

			inv.setItem(10, mining);
			inv.setItem(12, building);
			inv.setItem(14, mobkilling);
			inv.setItem(16, playerkilling);

			player.openInventory(inv);
			return true;
		}

		if (cmd.getLabel().equalsIgnoreCase("questcreate") && !(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Sorry, players only!");
			return true;
		}

		return false;
	}

}
