package me.fatpigsarefat.quests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reward {

	public Reward(Main plugin, Player player, String quest) {
		for (String s : plugin.getConfig().getStringList("quests." + quest + ".rewards")) {
			String[] parts = s.split(", ");
			String value = parts[1];
			value = value.replace("value:[", "");
			value = value.replace("]", "");
			value = value.replace("%player%", player.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value);
		}
	}
	
}
