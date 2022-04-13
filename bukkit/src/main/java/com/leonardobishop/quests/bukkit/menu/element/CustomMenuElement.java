package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CustomMenuElement extends MenuElement{

    private final ItemStack itemStack;
    private final List<String> commands;

    public CustomMenuElement(BukkitQuestsPlugin plugin, UUID owner, ItemStack itemStack) {
        this.itemStack = MenuUtils.applyPlaceholders(plugin, owner, itemStack);
        this.commands = new ArrayList<>();
    }

    public CustomMenuElement(BukkitQuestsPlugin plugin, UUID owner, ItemStack itemStack, List<String> commands) {
        this.itemStack = MenuUtils.applyPlaceholders(plugin, owner, itemStack);
        this.commands = commands;
    }

    @Override
    public ItemStack asItemStack() {
        return itemStack;
    }

    public List<String> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}
