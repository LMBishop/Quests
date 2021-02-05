package com.leonardobishop.quests.hooks.itemgetter;

import com.leonardobishop.quests.Quests;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ItemGetter_Late_1_8 implements ItemGetter {
    /*
     reads the following:
      - name
      - material (+ DATA)
      - lore
      - enchantments (NOT NamespacedKey)
      - itemflags

      requires at least API version 1.8 (?)
      */
    @Override
    public ItemStack getItem(String path, ConfigurationSection config, Quests plugin, Filter... excludes) {
        if (path != null && !path.equals("")) {
            path = path + ".";
        }
        List<Filter> filters = Arrays.asList(excludes);


        String cName = config.getString(path + "name", path + "name");
        String cType = config.getString(path + "item", config.getString(path + "type", path + "item"));
        List<String> cLore = config.getStringList(path + "lore");
        List<String> cItemFlags = config.getStringList(path + "itemflags");

        String name;
        Material type = null;
        int data = 0;

        // material
        ItemStack is = getItemStack(cType, plugin);
        ItemMeta ism = is.getItemMeta();

        // lore
        if (!filters.contains(Filter.LORE)) {
            List<String> lore = new ArrayList<>();
            if (cLore != null) {
                for (String s : cLore) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
            }
            ism.setLore(lore);
        }

        // name
        if (!filters.contains(Filter.DISPLAY_NAME)) {
            name = ChatColor.translateAlternateColorCodes('&', cName);
            ism.setDisplayName(name);
        }


        // item flags
        if (!filters.contains(Filter.ITEM_FLAGS)) {
            if (config.isSet(path + "itemflags")) {
                for (String flag : cItemFlags) {
                    for (ItemFlag iflag : ItemFlag.values()) {
                        if (iflag.toString().equals(flag)) {
                            ism.addItemFlags(iflag);
                            break;
                        }
                    }
                }
            }
        }

        // enchantments
        if (!filters.contains(Filter.ENCHANTMENTS)) {
            if (config.isSet(path + "enchantments")) {
                for (String key : config.getStringList(path + "enchantments")) {
                    String[] split = key.split(":");
                    String ench = split[0];
                    String levelName;
                    if (split.length >= 2) {
                        levelName = split[1];
                    } else {
                        levelName = "1";
                    }

                    Enchantment enchantment;
                    if ((enchantment = Enchantment.getByName(ench)) == null) {
                        plugin.getQuestsLogger().debug("Unrecognised enchantment: " + ench);
                        continue;
                    }

                    int level;
                    try {
                        level = Integer.parseInt(levelName);
                    } catch (NumberFormatException e) {
                        level = 1;
                    }

                    is.addUnsafeEnchantment(enchantment, level);
                }
            }
        }

        is.setItemMeta(ism);
        return is;
    }


    @Override
    public ItemStack getItemStack(String material, Quests plugin) {
        Material type = null;
        int data = 0;

        if (Material.getMaterial(material) != null) {
            type = Material.getMaterial(material);
        } else if (material.contains(":")) {
            String[] parts = material.split(Pattern.quote(":"));
            if (parts.length > 1) {
                if (Material.getMaterial(parts[0]) != null) {
                    type = Material.getMaterial(parts[0]);
                }
                if (StringUtils.isNumeric(parts[1])) {
                    data = Integer.parseInt(parts[1]);
                }
            }
        }

        if (type == null) {
            plugin.getQuestsLogger().debug("Unrecognised material: " + material);
            type = Material.STONE;
        }
        return new ItemStack(type, 1, (short) data);
    }

    @Override
    public boolean isValidMaterial(String material) {
        Material type = null;

        if (Material.getMaterial(material) != null) {
            type = Material.getMaterial(material);
        } else if (material.contains(":")) {
            String[] parts = material.split(Pattern.quote(":"));
            if (parts.length > 1) {
                if (Material.getMaterial(parts[0]) != null) {
                    type = Material.getMaterial(parts[0]);
                }
            }
        }

        return !(type == null);
    }
}
