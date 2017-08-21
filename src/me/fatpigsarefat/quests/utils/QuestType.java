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
	WALKING,
	TOTALEXP,
	EXP,
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
		case "WALKING":
			return QuestType.WALKING;
		case "TOTALEXP":
			return QuestType.TOTALEXP;
		case "TOTALEXPERIENCE":
			return QuestType.EXP;
		case "EXP":
			return QuestType.EXP;
		case "EXPERIENCE":
			return QuestType.EXP;
		case "CUSTOM":
			return QuestType.CUSTOM;
		default:
			return null;
		}
	}
	
}
