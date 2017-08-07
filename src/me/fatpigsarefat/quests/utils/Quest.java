package me.fatpigsarefat.quests.utils;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class Quest {

	private QuestType questType;
	private String nameId;
	private ItemStack displayItem;
	private boolean redoable;
	private boolean cooldownEnabled;
	private int cooldown;
	private ArrayList<String> rewards;
	private ArrayList<String> rewardString;
	private ArrayList<String> requirements;
	private String completionValue;
	private boolean worldsRestriced;
	private ArrayList<String> allowedWorlds;

	public Quest(QuestType questType, String nameId, ItemStack displayItem, boolean redoable, boolean cooldownEnabled,
			int cooldown, ArrayList<String> rewards, ArrayList<String> rewardString, ArrayList<String> requirements,
			String completionValue, boolean worldsRestricted, ArrayList<String> allowedWorlds) {
		this.questType = questType;
		this.nameId = nameId;
		this.displayItem = displayItem;
		this.redoable = redoable;
		this.cooldownEnabled = cooldownEnabled;
		this.cooldown = cooldown;
		this.rewards = rewards;
		this.rewardString = rewardString;
		this.requirements = requirements;
		this.completionValue = completionValue;
		this.worldsRestriced = worldsRestricted;
		this.allowedWorlds = allowedWorlds;
	}

	public Quest() {

	}

	public QuestType getQuestType() {
		return questType;
	}

	public void setQuestType(QuestType questType) {
		this.questType = questType;
	}

	public String getNameId() {
		return nameId;
	}

	public void setNameId(String nameId) {
		this.nameId = nameId;
	}

	public String getCompletionValue() {
		return completionValue;
	}

	public void setCompletionValue(String completionValue) {
		this.completionValue = completionValue;
	}

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public void setDisplayItem(ItemStack displayItem) {
		this.displayItem = displayItem;
	}

	public boolean isRedoable() {
		return redoable;
	}

	public void setRedoable(boolean redoable) {
		this.redoable = redoable;
	}

	public boolean isCoodlownEnabled() {
		return cooldownEnabled;
	}

	public void setCoodlownEnabled(boolean coodlownEnabled) {
		this.cooldownEnabled = coodlownEnabled;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public ArrayList<String> getRewards() {
		return rewards;
	}

	public void setRewards(ArrayList<String> rewards) {
		this.rewards = rewards;
	}

	public ArrayList<String> getRewardString() {
		return rewardString;
	}

	public void setRewardString(ArrayList<String> rewardString) {
		this.rewardString = rewardString;
	}

	public ArrayList<String> getRequirements() {
		return requirements;
	}

	public void setRequirements(ArrayList<String> requirements) {
		this.requirements = requirements;
	}

	public boolean isCooldownEnabled() {
		return cooldownEnabled;
	}

	public boolean isWorldsRestriced() {
		return worldsRestriced;
	}

	public ArrayList<String> getAllowedWorlds() {
		return allowedWorlds;
	}

}
