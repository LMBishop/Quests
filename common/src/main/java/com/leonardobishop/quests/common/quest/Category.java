package com.leonardobishop.quests.common.quest;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private final String id;
    private final boolean permissionRequired;
    private final List<String> registeredQuestIds = new ArrayList<>();

    public Category(String id, boolean permissionRequired) {
        this.id = id;
        this.permissionRequired = permissionRequired;
    }

    public String getId() {
        return id;
    }

    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    public void registerQuestId(String questid) {
        registeredQuestIds.add(questid);
    }

    public List<String> getRegisteredQuestIds() {
        return registeredQuestIds;
    }

}
