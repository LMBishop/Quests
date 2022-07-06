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

        if (item == null) {
            return null;
        }

        return item.buildItem(1, Optional.empty());
    }

    @Override
    public boolean compareItemStack(ItemStack other) {
        ExecutableItemInterface item = getExecutableItem();

        Optional<ExecutableItemInterface> otherItemOptional = executableItemsManager.getExecutableItem(other);
        if (item == null|| otherItemOptional.isEmpty()) {
            return false;
        }

        ExecutableItemInterface otherItem = otherItemOptional.get();

        return otherItem.getId().equals(item.getId());
    }

    private ExecutableItemInterface getExecutableItem() {
        if (!executableItemsManager.isValidID(executableItemsId)) {
            return null;
        }

        Optional<ExecutableItemInterface> itemOptional = executableItemsManager.getExecutableItem(executableItemsId);
        if (itemOptional.isEmpty()) {
            return null;
        }
        return itemOptional.get();
    }

}
