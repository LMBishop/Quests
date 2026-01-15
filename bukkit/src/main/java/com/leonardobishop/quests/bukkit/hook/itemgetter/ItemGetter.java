package com.leonardobishop.quests.bukkit.hook.itemgetter;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public abstract class ItemGetter {

    protected static final ItemStack INVALID_ITEM_STACK = new ItemStack(Material.STONE, 1);

    protected final BukkitQuestsPlugin plugin;

    public ItemGetter(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets an ItemStack from a configuration.
     * Implementations should specific to the server version.
     *
     * @param path     the path to where the item is defined in the config (null if item is defined in second param)
     * @param config   the configuration file
     * @param excludes exclude certain fields in the configuration
     * @return {@link ItemStack}
     */
    public abstract ItemStack getItem(String path, ConfigurationSection config, Filter... excludes);

    /**
     * Gets an ItemStack from a given string (which represents a material).
     * For pre-1.13 server implementations, the string may use a data code.
     *
     * @param typeString the string
     * @return {@link ItemStack}
     */
    public abstract ItemStack getItemStack(String typeString);

    /**
     * Validates a material from a string.
     * For pre-1.13 server implementations, the string may use a data code.
     *
     * @param typeString the string
     * @return true if it a material
     */
    public abstract boolean isValidMaterial(String typeString);

    public enum Filter {
        DISPLAY_NAME,
        LORE,
        ENCHANTMENTS,
        ITEM_FLAGS,
        UNBREAKABLE,
        ATTRIBUTE_MODIFIER,
        CUSTOM_MODEL_DATA,
        ITEM_MODEL,
        ENCHANTMENT_GLINT_OVERRIDE,
        HIDE_TOOLTIP
    }
}
