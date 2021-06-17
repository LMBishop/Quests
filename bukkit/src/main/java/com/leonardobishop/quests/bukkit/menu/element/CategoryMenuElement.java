package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.menu.QuestQMenu;
import com.leonardobishop.quests.common.quest.Category;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryMenuElement extends MenuElement {

    private final BukkitQuestsPlugin plugin;
    private final UUID owner;
    private final QuestQMenu questMenu;

    public CategoryMenuElement(BukkitQuestsPlugin plugin, UUID owner, QuestQMenu questMenu) {
        this.plugin = plugin;
        this.owner = owner;
        this.questMenu = questMenu;
    }

    public UUID getOwner() {
        return owner;
    }

    public QuestQMenu getQuestMenu() {
        return questMenu;
    }

    @Override
    public ItemStack asItemStack() {
        Category category = plugin.getQuestManager().getCategoryById(questMenu.getCategoryName());
        if (category != null) {
            return replaceItemStack(plugin.getQItemStackRegistry().getCategoryItemStack(category));
        }
        return null;
    }

    private ItemStack replaceItemStack(ItemStack is) {
        if (plugin.getPlaceholderAPIHook() != null && plugin.getQuestsConfig().getBoolean("options.gui-use-placeholderapi")) {
            ItemStack newItemStack = is.clone();
            List<String> lore = newItemStack.getItemMeta().getLore();
            List<String> newLore = new ArrayList<>();
            ItemMeta ism = newItemStack.getItemMeta();
            Player player = Bukkit.getPlayer(owner);
            ism.setDisplayName(plugin.getPlaceholderAPIHook().replacePlaceholders(player, ism.getDisplayName()));
            if (lore != null) {
                for (String s : lore) {
                    s = plugin.getPlaceholderAPIHook().replacePlaceholders(player, s);
                    newLore.add(s);
                }
            }
            ism.setLore(newLore);
            newItemStack.setItemMeta(ism);
            return newItemStack;
        }
        return is;
    }
}
