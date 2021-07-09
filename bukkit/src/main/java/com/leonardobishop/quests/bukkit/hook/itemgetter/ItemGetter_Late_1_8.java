package com.leonardobishop.quests.bukkit.hook.itemgetter;

import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ItemGetter_Late_1_8 implements ItemGetter {

    private Field profileField;

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
    public ItemStack getItem(String path, ConfigurationSection config, Filter... excludes) {
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
        ItemStack is = getItemStack(cType);
        ItemMeta ism = is.getItemMeta();

        // skull

        // skull
        if (is.getType().toString().equals("SKULL_ITEM")) {
            SkullMeta sm = (SkullMeta) ism;
            String cOwnerBase64 = config.getString(path + "owner-base64");
            String cOwnerUsername = config.getString(path + "owner-username");
            String cOwnerUuid = config.getString(path + "owner-uuid");
            if (cOwnerBase64 != null || cOwnerUsername != null || cOwnerUuid != null) {
                if (cOwnerUsername != null) {
                    sm.setOwner(cOwnerUsername);
                } else if (cOwnerUuid != null) {
                    try {
                        sm.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(cOwnerUuid)));
                    } catch (IllegalArgumentException ignored) { }
                } else {
                    GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                    profile.getProperties().put("textures", new Property("textures", cOwnerBase64));
                    if (profileField == null) {
                        try {
                            profileField = sm.getClass().getDeclaredField("profile");
                            profileField.setAccessible(true);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        profileField.set(sm, profile);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // lore
        if (!filters.contains(Filter.LORE)) {
            ism.setLore(Chat.color(cLore));
        }

        // name
        if (!filters.contains(Filter.DISPLAY_NAME)) {
            name = Chat.color(cName);
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
                        continue;
                    }

                    int level;
                    try {
                        level = Integer.parseInt(levelName);
                    } catch (NumberFormatException e) {
                        level = 1;
                    }

                    ism.addEnchant(enchantment, level, true);
                }
            }
        }

        is.setItemMeta(ism);
        return is;
    }


    @Override
    public ItemStack getItemStack(String material) {
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
