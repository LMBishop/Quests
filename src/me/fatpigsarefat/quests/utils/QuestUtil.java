package me.fatpigsarefat.quests.utils;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import me.fatpigsarefat.quests.utils.values.MobCertainValue;
import me.fatpigsarefat.quests.utils.values.NormalCertainValue;

public class QuestUtil {

	private static int parseNonCertainInteger(Quest quest) {
		int value = 0;
		try {
			value = Integer.parseInt(quest.getCompletionValue());
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return value;
	}
	
	private static NormalCertainValue parseCertainValue(Quest quest) {
		String[] parts = quest.getCompletionValue().split(":");
		try {
			return new NormalCertainValue(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private static MobCertainValue parseMobCertainValue(Quest quest) {
		String[] parts = quest.getCompletionValue().split(":");
		try {
			return new MobCertainValue(EntityType.fromName(parts[0]), Integer.parseInt(parts[1]));
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String timeConvert(int time) {
		return time / 24 / 60 + "d " + time / 60 % 24 + "h " + time % 60 + "m";
	}

	public static int parseMiningValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public static int parseBuildingValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public static int parseMobkillingValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public static int parsePlayerkillingValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public static int parseAskyblockValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public static int parseUskyblockValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public static int parseTimeplayedValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}
	
	public static int parseExperienceValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public static int parseWalkingValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}


	public static NormalCertainValue parseMiningCertainValue(Quest quest) {
		return parseCertainValue(quest);
	}

	public static NormalCertainValue parseBuildingCertainValue(Quest quest) {
		return parseCertainValue(quest);
	}

	public static MobCertainValue parseMobkillingCertainValue(Quest quest) {
		return parseMobCertainValue(quest);
	}
	
	@SuppressWarnings("deprecation")
	public static ArrayList<ItemStack> parseInventoryValue(Quest quest) {
		String value = quest.getCompletionValue();
		value = value.replace("[", "");
		value = value.replace("]", "");
		ArrayList<ItemStack> requiredMaterials = new ArrayList<ItemStack>();
		if (value.contains(", ")) {
			String[] parts = value.split(", ");
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
						continue;
					}
				} else {
					if (Material.getMaterial(Integer.parseInt(material)) == null) {
						continue;
					}
				}
				ItemStack is = new ItemStack(isIdNotString ? Material.getMaterial(Integer.parseInt(material))
						: Material.getMaterial(material), amount);
				requiredMaterials.add(is);
			}
		} else {
			int amount = 1;
			String material = value;
			if (value.contains(":")) {
				String[] parts = value.split(":");
				amount = Integer.parseInt(parts[1]);
				material = parts[0];
			}
			if (Material.getMaterial(material) == null) {
				return null;
			}
			ItemStack is = new ItemStack(Material.getMaterial(material), amount);
			requiredMaterials.add(is);
		}
		return requiredMaterials;
	}
	
}
