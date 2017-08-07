package me.fatpigsarefat.quests.utils.values;

import org.bukkit.entity.EntityType;

public class MobCertainValue {

	private EntityType entity;
	private int neededToKill;
	
	public MobCertainValue(EntityType entity, int neededToKill) {
		this.entity = entity;
		this.neededToKill = neededToKill;
	}
	
	public EntityType getEntity() {
		return entity;
	}
	
	public int getNeededToKill() {
		return neededToKill;
	}
	
}
