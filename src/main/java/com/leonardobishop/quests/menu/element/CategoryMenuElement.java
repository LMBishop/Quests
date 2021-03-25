package com.leonardobishop.quests.menu.element;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.menu.QuestQMenu;
import com.leonardobishop.quests.quests.Category;
import com.leonardobishop.quests.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryMenuElement extends MenuElement {

    private final Quests plugin;
    private final UUID owner;
    private final QuestQMenu questMenu;

    public CategoryMenuElement(Quests plugin, UUID owner, QuestQMenu questMenu) {
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
            return replaceItemStack(category.getDisplayItem());
        }
        return null;
    }

    private ItemStack replaceItemStack(ItemStack is) {
        if (plugin.getPlaceholderAPIHook() != null && Options.GUI_USE_PLACEHOLDERAPI.getBooleanValue()) {
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
