package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.menu.ClickResult;
import com.leonardobishop.quests.bukkit.util.DispatchUtils;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CustomMenuElement extends MenuElement{

    private final BukkitQuestsPlugin plugin;
    private final ItemStack itemStack;
    private final List<String> commands;

    public CustomMenuElement(BukkitQuestsPlugin plugin, UUID owner, ItemStack itemStack) {
        this(plugin, owner, itemStack, new ArrayList<>());
    }

    public CustomMenuElement(BukkitQuestsPlugin plugin, UUID owner, ItemStack itemStack, List<String> commands) {
        this.plugin = plugin;
        this.itemStack = MenuUtils.applyPlaceholders(plugin, owner, itemStack);
        this.commands = commands;
    }

    @Override
    public ItemStack asItemStack() {
        return itemStack;
    }

    @Override
    public ClickResult handleClick(Player whoClicked, ClickType clickType) {
        if (commands.isEmpty()) {
            return ClickResult.DO_NOTHING;
        }

        this.plugin.getScheduler().runTask(() -> {
            for (String command : commands) {
                DispatchUtils.dispatchCommand(whoClicked, this.plugin.applyPlayerAndPAPI(BukkitQuestsPlugin.PAPIType.GUI, whoClicked, command));
            }
        });

        return ClickResult.DO_NOTHING;
    }

    public List<String> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}
