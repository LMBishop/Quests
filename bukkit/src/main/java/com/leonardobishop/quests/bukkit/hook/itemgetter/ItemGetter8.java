package com.leonardobishop.quests.bukkit.hook.itemgetter;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * Reads the following:
 * <ul>
 *     <li>type (<b>with</b> data support, <b>without</b> namespace support)</li>
 *     <li>name</li>
 *     <li>lore</li>
 *     <li>enchantments (<b>without</b> namespace support)</li>
 *     <li>item flags</li>
 *     <li>unbreakability (<b>without</b> CraftBukkit support)</li>
 * </ul>
 * Requires at least API version 1.8.
 */
@SuppressWarnings({"deprecation", "DuplicatedCode", "JavaReflectionMemberAccess"})
public class ItemGetter8 extends ItemGetter {

    private static Method spigotMethod;
    private static Method setUnbreakableMethod;

    static {
        try {
            spigotMethod = ItemMeta.class.getMethod("spigot"); // removed in 1.15

            Class<?> spigotClass = Class.forName("org.bukkit.inventory.meta.ItemMeta.Spigot");
            setUnbreakableMethod = spigotClass.getMethod("setUnbreakable", boolean.class);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            // server version cannot support the method (doesn't work on CraftBukkit)
        }
    }

    public ItemGetter8(BukkitQuestsPlugin plugin) {
        super(plugin);
    }

    @Override
    public ItemStack getItem(String path, ConfigurationSection config, Filter... excludes) {
        config = config.getConfigurationSection(path);
        if (config == null) {
            return INVALID_ITEM_STACK;
        }

        Set<Filter> filters = Set.of(excludes);

        // type (with data)
        String typeString = config.getString("item", config.getString("type"));
        ItemStack item = getItemStack(typeString);
        ItemMeta meta = item.getItemMeta();

        // skull
        if (meta instanceof SkullMeta skullMeta) {
            String ownerName = config.getString("owner-username");
            String ownerUniqueIdString = config.getString("owner-uuid");
            String ownerBase64 = config.getString("owner-base64");

            plugin.getSkullGetter().apply(skullMeta, ownerName, ownerUniqueIdString, ownerBase64);
        }

        // name
        String nameString = config.getString("name");
        if (nameString != null && !filters.contains(Filter.DISPLAY_NAME)) {
            nameString = Chat.legacyColor(nameString);

            meta.setDisplayName(nameString);
        }

        // lore
        List<String> loreStrings = config.getStringList("lore");
        if (!loreStrings.isEmpty() && !filters.contains(Filter.LORE)) {
            loreStrings = Chat.legacyColor(loreStrings);

            meta.setLore(loreStrings);
        }

        // enchantments
        List<String> enchantmentStrings = config.getStringList("enchantments");
        if (!enchantmentStrings.isEmpty() && !filters.contains(Filter.ENCHANTMENTS)) {
            for (String enchantmentString : enchantmentStrings) {
                String[] parts = enchantmentString.split(":");
                if (parts.length == 0) {
                    continue;
                }

                Enchantment enchantment = Enchantment.getByName(parts[0]);
                if (enchantment == null) {
                    continue;
                }

                int level;
                if (parts.length == 2) {
                    try {
                        level = Integer.parseUnsignedInt(parts[1]);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                } else if (parts.length == 1) {
                    level = 1;
                } else {
                    continue;
                }

                meta.addEnchant(enchantment, level, true);
            }
        }

        // item flags
        List<String> itemFlagStrings = config.getStringList("itemflags");
        if (!itemFlagStrings.isEmpty() && !filters.contains(Filter.ITEM_FLAGS)) {
            for (String itemFlagString : itemFlagStrings) {
                ItemFlag itemFlag;
                try {
                    itemFlag = ItemFlag.valueOf(itemFlagString);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                meta.addItemFlags(itemFlag);
            }
        }

        if (spigotMethod != null && setUnbreakableMethod != null) {
            // unbreakability
            Boolean unbreakable = (Boolean) config.get("unbreakable");
            if (unbreakable != null && !filters.contains(Filter.UNBREAKABLE)) {
                try {
                    setUnbreakableMethod.invoke(spigotMethod.invoke(meta), unbreakable);
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                }
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ItemStack getItemStack(String typeString) {
        if (typeString == null) {
            return INVALID_ITEM_STACK;
        }

        Material type = Material.getMaterial(typeString);
        if (type != null) {
            return new ItemStack(type, 1);
        }

        String[] parts = typeString.split(":");
        if (parts.length != 2) {
            return INVALID_ITEM_STACK;
        }

        type = Material.getMaterial(parts[0]);
        if (type == null) {
            return INVALID_ITEM_STACK;
        }

        byte data;
        try {
            data = Byte.parseByte(parts[1]);
        } catch (NumberFormatException ignored) {
            return INVALID_ITEM_STACK;
        }

        return new ItemStack(type, 1, (short) 0, data);
    }

    @Override
    public boolean isValidMaterial(String typeString) {
        if (Material.getMaterial(typeString) != null) {
            return true;
        }

        String[] parts = typeString.split(":");
        return parts.length == 2 && Material.getMaterial(parts[0]) != null;
    }
}
