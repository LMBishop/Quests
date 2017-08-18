package me.fatpigsarefat.quests.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.utils.values.MobCertainValue;
import me.fatpigsarefat.quests.utils.values.NormalCertainValue;
import net.md_5.bungee.api.ChatColor;

public class QuestData {

	public ItemStack getCompleteItemStack() {
		Material mat = null;
		int id = 0;
		ItemStack notRedoable;
		if (Quests.getInstance().getConfig().getString("gui.completed.item").contains(":")) {
			String[] st = Quests.getInstance().getConfig().getString("gui.completed.item").split(":");
			mat = Material.getMaterial(st[0]);
			id = Integer.parseInt(st[1]);
			notRedoable = new ItemStack(mat, 1, (byte) id);
		} else {
			notRedoable = new ItemStack(mat, 1);
		}
		ItemMeta notRedoableM = notRedoable.getItemMeta();
		notRedoableM.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				Quests.getInstance().getConfig().getString("gui.completed.name")));
		List<String> lore = new ArrayList<String>();
		if (Quests.getInstance().getConfig().contains("gui.completed.lore")) {
			for (String str : Quests.getInstance().getConfig().getStringList("gui.completed.lore")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', str));
			}
		}
		notRedoableM.setLore(lore);
		notRedoable.setItemMeta(notRedoableM);
		return notRedoable;
	}

	public ItemStack getCooldownItemStack(Quest quest, UUID uuid) {
		Material mat = null;
		int id = 0;
		ItemStack notRedoable;
		if (Quests.getInstance().getConfig().getString("gui.cooldown.item").contains(":")) {
			String[] st = Quests.getInstance().getConfig().getString("gui.cooldown.item").split(":");
			mat = Material.getMaterial(st[0]);
			id = Integer.parseInt(st[1]);
			notRedoable = new ItemStack(mat, 1, (byte) id);
		} else {
			notRedoable = new ItemStack(mat, 1);
		}
		ItemMeta notRedoableM = notRedoable.getItemMeta();
		notRedoableM.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				Quests.getInstance().getConfig().getString("gui.cooldown.name")));
		List<String> lore = new ArrayList<String>();
		if (Quests.getInstance().getConfig().contains("gui.cooldown.lore")) {
			for (String str : Quests.getInstance().getConfig().getStringList("gui.cooldown.lore")) {
				if (str.contains("%cooldown%")) {
					if (isOnCooldown(quest, uuid)) {
						str = str.replace("%cooldown%", convertToFormat(getCooldown(quest, uuid)));
					}
				}
				lore.add(ChatColor.translateAlternateColorCodes('&', str));
			}
		}
		notRedoableM.setLore(lore);
		notRedoable.setItemMeta(notRedoableM);
		return notRedoable;
	}

	public ItemStack getLockedItemStack() {
		Material mat = null;
		int id = 0;
		ItemStack notRedoable;
		if (Quests.getInstance().getConfig().getString("gui.locked.item").contains(":")) {
			String[] st = Quests.getInstance().getConfig().getString("gui.locked.item").split(":");
			mat = Material.getMaterial(st[0]);
			id = Integer.parseInt(st[1]);
			notRedoable = new ItemStack(mat, 1, (byte) id);
		} else {
			notRedoable = new ItemStack(mat, 1);
		}
		ItemMeta notRedoableM = notRedoable.getItemMeta();
		notRedoableM.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				Quests.getInstance().getConfig().getString("gui.locked.name")));
		List<String> lore = new ArrayList<String>();
		if (Quests.getInstance().getConfig().contains("gui.locked.lore")) {
			for (String str : Quests.getInstance().getConfig().getStringList("gui.locked.lore")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', str));
			}
		}
		notRedoableM.setLore(lore);
		notRedoable.setItemMeta(notRedoableM);
		return notRedoable;
	}

	public boolean hasCompletedQuestBefore(String quest, UUID uuid) {
		if (getData().contains("progress." + uuid + ".quests-completed")) {
			if (getData().getStringList("progress." + uuid + ".quests-completed").contains(quest)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOnCooldown(Quest quest, UUID uuid) {
		if (getData().contains("progress." + uuid + ".quests-cooldown." + quest.getNameId())) {
			return true;
		}
		return false;
	}

	public int getCooldown(Quest quest, UUID uuid) {
		if (getData().contains("progress." + uuid + ".quests-cooldown." + quest.getNameId())) {
			return getData().getInt("progress." + uuid + ".quests-cooldown." + quest.getNameId());
		}
		return 0;
	}

	public void setCooldown(Quest quest, UUID uuid, int cooldown) {
		YamlConfiguration yaml = getData();
		yaml.set("progress." + uuid + ".quests-cooldown." + quest.getNameId(), cooldown);
		saveData(yaml);
	}

	@SuppressWarnings("unchecked")
	public boolean hasMetRequirements(Quest quest, UUID uuid) {
		ArrayList<String> requirements = (ArrayList<String>) quest.getRequirements().clone();
		ArrayList<String> requirementsEdited = (ArrayList<String>) quest.getRequirements().clone();
		if (!requirements.isEmpty()) {
			for (String s : requirements) {
				Quest q = Quests.getInstance().getQuestManager().getQuestById(s);
				if (hasCompletedQuestBefore(q.getNameId(), uuid)) {
					requirementsEdited.remove(q.getNameId());
				}
			}
		}
		if (requirementsEdited.isEmpty()) {
			return true;
		}
		return false;
	}

	public ItemStack getDisplayItemReplaced(Quest quest, UUID uuid) {
		ItemStack is = quest.getDisplayItem().clone();
		List<String> lore = is.getItemMeta().getLore();
		List<String> newLore = new ArrayList<String>();
		for (String s : lore) {
			if (s.contains("%progress%")) {
				if (quest.getQuestType() == QuestType.TIMEPLAYED) {
					s = s.replace("%progress%", timeConvert(getTimePlayed(uuid)));
				} else {
					if (hasProgress(quest, uuid)) {
						s = s.replace("%progress%", String.valueOf(getProgress(quest, uuid)));
					} else {
						s = s.replace("%progress%", String.valueOf(0));
					}
				}
			}
			newLore.add(s);
		}
		if (Quests.getInstance().getQuestManager().getSelectorMode() == SelectorType.RANDOM) {
			newLore.add(ChatColor.translateAlternateColorCodes('&',
					Quests.getInstance().getConfig().getString("quest-settings.all.expire-string").replace("%time%",
							convertToFormat(getRandomQuestsTimeRemaining(uuid)))));
		}
		ItemMeta ism = is.getItemMeta();
		ism.setLore(newLore);
		if (hasStartedQuest(quest, uuid)) {
			ism.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
			if (Quests.getInstance().isTitleEnabled()) {
				ism.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
		}
		is.setItemMeta(ism);
		return is;
	}

	public boolean hasStartedQuest(Quest quest, UUID uuid) {
		if (getData().contains("progress." + uuid + ".quests-progress." + quest.getNameId())) {
			return true;
		}
		return false;
	}

	public List<String> getStartedQuests(UUID uuid) {
		return getData().getStringList("progress." + uuid + ".quests-started");
	}

	public boolean startQuest(UUID uuid, Quest quest) {
		YamlConfiguration data = getData();
		data.set("progress." + uuid + ".name", Bukkit.getOfflinePlayer(uuid).getName());
		List<String> questsStarted = new ArrayList<String>();
		if (data.contains("progress." + uuid + ".quests-started")) {
			questsStarted = data.getStringList("progress." + uuid + ".quests-started");
		}
		if (questsStarted.contains(quest.getNameId())) {
			Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "Quest already started!");
			return false;
		}
		questsStarted.add(quest.getNameId());
		data.set("progress." + uuid + ".quests-started", questsStarted);
		data.set("progress." + uuid + ".quests-progress." + quest.getNameId() + ".value", 0);
		saveData(data);
		return true;
	}

	public int getProgress(Quest quest, UUID uuid) {
		return getData().getInt("progress." + uuid + ".quests-progress." + quest.getNameId() + ".value");
	}

	public boolean hasProgress(Quest quest, UUID uuid) {
		if (getData().contains("progress." + uuid + ".quests-progress." + quest.getNameId() + ".value")) {
			return true;
		}
		return false;
	}

	public void addProgress(Quest quest, UUID uuid) {
		int value = getProgress(quest, uuid);
		value++;
		YamlConfiguration data = getData();
		data.set("progress." + uuid + ".quests-progress." + quest.getNameId() + ".value", value);
		saveData(data);
	}

	public int getTimePlayed(UUID uuid) {
		if (getData().contains("progress." + uuid + ".time-played")) {
			return getData().getInt("progress." + uuid + ".time-played");
		} else {
			return 0;
		}
	}

	public void setTimePlayed(UUID uuid, int amount) {
		YamlConfiguration data = getData();
		data.set("progress." + uuid + ".time-played", amount);
		saveData(data);
	}

	public void completeQuest(Quest quest, UUID uuid) {
		YamlConfiguration data = getData();
		List<String> questsStarted = getStartedQuests(uuid);
		dispatchReward(uuid, quest);
		int i = 0;
		for (String q : questsStarted) {
			if (q.equals(quest.getNameId())) {
				questsStarted.remove(i);
				break;
			}
			i++;
		}
		data.set("progress." + uuid + ".quests-progress." + quest.getNameId(), null);
		data.set("progress." + uuid + ".quests-started", questsStarted);
		List<String> questsCompleted = new ArrayList<>();
		if (data.contains("progress." + uuid + ".quests-completed")) {
			questsCompleted = data.getStringList("progress." + uuid + ".quests-completed");
		}

		int cooldownTime = 0;
		if (quest.isCoodlownEnabled()) {
			cooldownTime = quest.getCooldown();
		}

		if (!(cooldownTime == 0)) {
			data.set("progress." + uuid + ".quests-cooldown." + quest.getNameId(), cooldownTime);
		}
		questsCompleted.add(quest.getNameId());

		data.set("progress." + uuid + ".quests-completed", questsCompleted);
		saveData(data);
	}
	
	public int getAmountOfCompletedQuests(UUID uuid) {
		YamlConfiguration data = getData();
		if (data.contains("progress." + uuid + ".quests-completed")) {
			return data.getStringList("progress." + uuid + ".quests-completed").size();
		}
		return 0;
	}

	public void dispatchReward(UUID uuid, Quest quest) {
		Player player = Bukkit.getPlayer(uuid);
		new Reward(player, quest);
		String titleMessage = "";
		String titleSubMessage = "";
		if (Quests.getInstance().getConfig().getString("title.enabled").equals("true")) {
			if (Quests.getInstance().isTitleEnabled()) {
				titleMessage = ChatColor.translateAlternateColorCodes('&',
						Quests.getInstance().getConfig().getString("title.mainmessage"));
				titleMessage = titleMessage.replace("%quest%", ChatColor.translateAlternateColorCodes('&',
						quest.getDisplayItem().getItemMeta().getDisplayName()));
				titleSubMessage = ChatColor.translateAlternateColorCodes('&',
						Quests.getInstance().getConfig().getString("title.submessage"));
				titleSubMessage = titleSubMessage.replace("%quest%", ChatColor.translateAlternateColorCodes('&',
						quest.getDisplayItem().getItemMeta().getDisplayName()));
				if (!quest.getRewardString().isEmpty()) {
					String rewardString = "";
					boolean comma = false;
					boolean finalb = false;
					int pos = 0;
					for (String st : quest.getRewardString()) {
						if (pos + 1 == quest.getRewardString().size()) {
							finalb = true;
						}
						rewardString = rewardString + (comma ? ", " : "")
								+ ChatColor.translateAlternateColorCodes('&', st) + (finalb ? "" : " ");
						comma = true;
						pos++;
					}
					titleMessage = titleMessage.replace("%rewardstring%",
							ChatColor.translateAlternateColorCodes('&', rewardString));
					titleSubMessage = titleSubMessage.replace("%rewardstring%",
							ChatColor.translateAlternateColorCodes('&', rewardString));
				}
				try {
					Quests.getInstance().getTitle().sendTitle(player, titleMessage, titleSubMessage);
				} catch (Exception e) {
					player.sendMessage(ChatColor.RED + "An exception occured whilst trying to show the title: "
							+ e.getMessage() + ". Title will not be shown.");
				}
			}
		}
		player.sendMessage(Messages.COMPLETE_QUEST.getMessage().replace("%quest%", ChatColor.translateAlternateColorCodes('&',
				quest.getDisplayItem().getItemMeta().getDisplayName())));
		if (Quests.getInstance().getConfig().getBoolean("show-rewardstring")) {
			if (!quest.getRewardString().isEmpty()) {
				player.sendMessage(Messages.REWARDS.getMessage());
				for (String st : quest.getRewardString()) {
					player.sendMessage(Messages.REWARD_STRING_FORMAT.getMessage().replace("%rewardstring%", ChatColor.translateAlternateColorCodes('&', st)));
				}
			}
		}
	}

	private YamlConfiguration getData() {
		File d = new File(Quests.getInstance().getDataFolder() + File.separator + "data.yml");
		YamlConfiguration data = YamlConfiguration.loadConfiguration((File) d);
		return data;
	}

	private void saveData(YamlConfiguration yaml) {
		File d = new File(Quests.getInstance().getDataFolder() + File.separator + "data.yml");
		try {
			yaml.save(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int parseNonCertainInteger(Quest quest) {
		int value = 0;
		try {
			value = Integer.parseInt(quest.getCompletionValue());
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return value;
	}

	private NormalCertainValue parseCertainValue(Quest quest) {
		String[] parts = quest.getCompletionValue().split(":");
		try {
			return new NormalCertainValue(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private MobCertainValue parseMobCertainValue(Quest quest) {
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

	public String timeConvert(int time) {
		return time / 24 / 60 + "d " + time / 60 % 24 + "h " + time % 60 + "m";
	}

	public int parseMiningValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public int parseBuildingValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public int parseMobkillingValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public int parsePlayerkillingValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public int parseAskyblockValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public int parseUskyblockValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public int parseTimeplayedValue(Quest quest) {
		return parseNonCertainInteger(quest);
	}

	public NormalCertainValue parseMiningCertainValue(Quest quest) {
		return parseCertainValue(quest);
	}

	public NormalCertainValue parseBuildingCertainValue(Quest quest) {
		return parseCertainValue(quest);
	}

	public MobCertainValue parseMobkillingCertainValue(Quest quest) {
		return parseMobCertainValue(quest);
	}

	@SuppressWarnings("deprecation")
	public ArrayList<ItemStack> parseInventoryValue(Quest quest) {
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

	@SuppressWarnings("deprecation")
	public boolean generateNewRandomQuests(UUID uuid) {
		ArrayList<String> newQuests = new ArrayList<String>();
		if (Quests.getInstance().getQuestManager().getQuests().size() < 5) {
			return false;
		}
		while (true) {
			Random random = new Random();
			int randomQuest = random.nextInt(Quests.getInstance().getQuestManager().getQuests().size() - 1);
			if (!newQuests.contains(Quests.getInstance().getQuestManager().getQuests().get(randomQuest).getNameId())) {
				newQuests.add(Quests.getInstance().getQuestManager().getQuests().get(randomQuest).getNameId());
			}
			if (newQuests.size() >= 5) {
				break;
			}
		}
		YamlConfiguration data = getData();
		data.set("progress." + uuid.toString() + ".random-quests.quests", newQuests);
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		now.setMinutes(now.getMinutes()
				+ Quests.getInstance().getConfig().getInt("quest-settings.all.random-method-refresh-rate"));
		data.set("progress." + uuid.toString() + ".random-quests.exipry", now.getTime());
		data.set("progress." + uuid.toString() + ".quests-started", null);
		data.set("progress." + uuid.toString() + ".quests-progress", null);
		data.set("progress." + uuid.toString() + ".quests-cooldown", null);
		data.set("progress." + uuid.toString() + ".quests-completed", null);
		saveData(data);
		return true;
	}

	public List<String> getRandomQuests(UUID uuid) {
		YamlConfiguration data = getData();
		if (data.contains("progress." + uuid.toString() + ".random-quests.quests")) {
			return data.getStringList("progress." + uuid.toString() + ".random-quests.quests");
		} else if (generateNewRandomQuests(uuid)) {
			data = null;
			data = getData();
			return data.getStringList("progress." + uuid.toString() + ".random-quests.quests");
		}
		return new ArrayList<String>();
	}

	public long getRandomQuestsTimeRemaining(UUID uuid) {
		YamlConfiguration data = getData();
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		Date then = new Date(data.getLong("progress." + uuid.toString() + ".random-quests.exipry", now.getTime()));
		return TimeUnit.MILLISECONDS.toMinutes(then.getTime() - now.getTime());
	}

	private String convertToFormat(int m) {
		int hours = m / 60;
		int minutesLeft = m - hours * 60;

		String formattedTime = "";

		if (hours < 10)
			formattedTime = formattedTime + "0";
		formattedTime = formattedTime + hours + "h";

		formattedTime = formattedTime + " ";

		if (minutesLeft < 10)
			formattedTime = formattedTime + "0";
		formattedTime = formattedTime + minutesLeft + "m";

		return formattedTime;
	}
	
	private String convertToFormat(long m) {
		int hours = (int) (m / 60);
		int minutesLeft = (int) (m - hours * 60);

		String formattedTime = "";

		if (hours < 10)
			formattedTime = formattedTime + "0";
		formattedTime = formattedTime + hours + "h";

		formattedTime = formattedTime + " ";

		if (minutesLeft < 10)
			formattedTime = formattedTime + "0";
		formattedTime = formattedTime + minutesLeft + "m";

		return formattedTime;
	}
}
