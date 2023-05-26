package com.leonardobishop.quests.bukkit.item;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.ssomar.score.api.executableitems.config.ExecutableItemsManagerInterface;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ExecutableItemsQuestItem extends QuestItem {

    private final String executableItemsId;
    private final ExecutableItemsManagerInterface executableItemsManager;

    public ExecutableItemsQuestItem(String id, String executableItemsId) {
        super("executableitems", id);
        this.executableItemsId = executableItemsId;
        this.executableItemsManager = ExecutableItemsAPI.getExecutableItemsManager();
    }

    @Override
    public ItemStack getItemStack() {
        ExecutableItemInterface item = getExecutableItem();
        return item != null ? item.buildItem(1, Optional.empty()) : null;
    }

    @Override
    public boolean compareItemStack(ItemStack other, boolean exactMatch) {
        ExecutableItemInterface item = getExecutableItem();
        if (item == null) {
            return false;
        }

        return executableItemsManager.getExecutableItem(other)
                .map(executableItemInterface -> {
                    final String itemId = item.getId();
                    return executableItemInterface.getId().equals(itemId);
                })
                .orElse(false);
    }

    private ExecutableItemInterface getExecutableItem() {
        return executableItemsManager.isValidID(executableItemsId)
                ? executableItemsManager.getExecutableItem(executableItemsId).orElse(null)
                : null;
    }
}
