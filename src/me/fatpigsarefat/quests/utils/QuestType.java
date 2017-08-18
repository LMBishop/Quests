package me.fatpigsarefat.quests.utils;

public enum QuestType {

	MINING,
	MININGCERTAIN,
	BUILDING,
	BUILDINGCERTAIN,
	MOBKILLING,
	MOBKILLINGCERTAIN,
	PLAYERKILLING,
	INVENTORY,
	ASKYBLOCK,
	USKYBLOCK,
	TIMEPLAYED,
	CUSTOM;
	
	public static QuestType fromString(String questType) {
		switch (questType.toUpperCase()) {
		case "MINING":
			return QuestType.MINING;
		case "MININGCERTAIN":
			return QuestType.MININGCERTAIN;
		case "BUILDING":
			return QuestType.BUILDING;
		case "BUILDINGCERTAIN":
			return QuestType.BUILDINGCERTAIN;
		case "MOBKILLING":
			return QuestType.MOBKILLING;
		case "MOBKILLINGCERTAIN":
			return QuestType.MOBKILLINGCERTAIN;
		case "PLAYERKILLING":
			return QuestType.PLAYERKILLING;
		case "INVENTORY":
			return QuestType.INVENTORY;
		case "ASKYBLOCK":
			return QuestType.ASKYBLOCK;
		case "USKYBLOCK":
			return QuestType.USKYBLOCK;
		case "TIMEPLAYED":
			return QuestType.TIMEPLAYED;
		case "CUSTOM":
			return QuestType.CUSTOM;
		default:
			return null;
		}
	}
	
}
