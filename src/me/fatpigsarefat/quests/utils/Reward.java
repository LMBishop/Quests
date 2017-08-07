package me.fatpigsarefat.quests.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.fatpigsarefat.quests.Quests;

public class Reward {

	public Reward(Player player, Quest quest) {
		for (String s : Quests.getInstance().getConfig().getStringList("quests." + quest.getNameId() + ".rewards")) {
			String[] parts = s.split(", ");
			String value = parts[1];
			value = value.replace("value:[", "");
			value = value.replace("]", "");
			value = value.replace("%player%", player.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value);
		}
	}
	
}
