package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.menu.ClickResult;
import com.leonardobishop.quests.bukkit.util.DispatchUtils;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CustomMenuElement extends MenuElement {

    private final BukkitQuestsPlugin plugin;
    private final ItemStack itemStack;
    private final List<String> commands;
    private final ClickResult result;

    public CustomMenuElement(BukkitQuestsPlugin plugin, UUID owner, ItemStack itemStack) {
        this(plugin, owner, itemStack, List.of(), ClickResult.DO_NOTHING);
    }

    public CustomMenuElement(BukkitQuestsPlugin plugin, UUID owner, ItemStack itemStack, List<String> commands, ClickResult result) {
        this.plugin = plugin;
        this.itemStack = MenuUtils.applyPlaceholders(plugin, owner, itemStack);
        this.commands = commands;
        this.result = result;
    }

    @Override
    public ItemStack asItemStack() {
        return this.itemStack;
    }

    @Override
    public ClickResult handleClick(final Player whoClicked, final ClickType clickType) {
        if (this.commands.isEmpty()) {
            return this.result;
        }

        this.plugin.getScheduler().runTask(() -> {
            for (final String command : this.commands) {
                DispatchUtils.dispatchCommand(whoClicked, this.plugin.applyPlayerAndPAPI(BukkitQuestsPlugin.PAPIType.GUI, whoClicked, command));
            }
        });

        return this.result;
    }

    public List<String> getCommands() {
        return Collections.unmodifiableList(this.commands);
    }
}
