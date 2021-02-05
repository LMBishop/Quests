package com.leonardobishop.quests.hooks.itemgetter;

import com.leonardobishop.quests.Quests;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemGetter_1_13 implements ItemGetter {
    /*
     reads the following:
      - name
      - material
      - lore
      - enchantments (NamespacedKey)
      - itemflags
      - unbreakable
      - attribute modifier

      requires at least API version 1.13
      */
    @Override
    public ItemStack getItem(String path, ConfigurationSection config, Quests plugin, ItemGetter.Filter... excludes) {
        if (path != null && !path.equals("")) {
            path = path + ".";
        }
        List<Filter> filters = Arrays.asList(excludes);

        String cName = config.getString(path + "name", path + "name");
        String cType = config.getString(path + "item", config.getString(path + "type", path + "item"));
        boolean unbreakable = config.getBoolean(path + "unbreakable", false);
        List<String> cLore = config.getStringList(path + "lore");
        List<String> cItemFlags = config.getStringList(path + "itemflags");
        boolean hasAttributeModifiers = config.contains(path + "attributemodifiers");
        List<Map<?, ?>> cAttributeModifiers = config.getMapList(path + "attributemodifiers");

        String name;
        Material type = null;
        int data = 0;

        // material
        ItemStack is = getItemStack(cType, plugin);
        ItemMeta ism = is.getItemMeta();

        // name
        if (!filters.contains(Filter.DISPLAY_NAME)) {
            name = ChatColor.translateAlternateColorCodes('&', cName);
            ism.setDisplayName(name);
        }

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

        // attribute modifiers
        if (!filters.contains(Filter.ATTRIBUTE_MODIFIER)) {
            if (hasAttributeModifiers) {
                for (Map<?, ?> attr : cAttributeModifiers) {
                    String cAttribute = (String) attr.get("attribute");
                    Attribute attribute = null;
                    for (Attribute enumattr : Attribute.values()) {
                        if (enumattr.toString().equals(cAttribute)) {
                            attribute = enumattr;
                            break;
                        }
                    }

                    if (attribute == null) continue;

                    Map<?, ?> configurationSection = (Map<?, ?>) attr.get("modifier");

                    String cUUID = (String) configurationSection.get("uuid");
                    String cModifierName = (String) configurationSection.get("name");
                    String cModifierOperation = (String) configurationSection.get("operation");
                    double cAmount;
                    try {
                        Object cAmountObj = configurationSection.get("amount");
                        if (cAmountObj instanceof Integer) {
                            cAmount = ((Integer) cAmountObj).doubleValue();
                        } else {
                            cAmount = (Double) cAmountObj;
                        }
                    } catch (Exception e) {
                        cAmount = 1;
                    }
                    String cEquipmentSlot = (String) configurationSection.get("equipmentslot");

                    UUID uuid = null;
                    if (cUUID != null) {
                        try {
                            uuid = UUID.fromString(cUUID);
                        } catch (Exception ignored) {
                            // ignored
                        }
                    }
                    EquipmentSlot equipmentSlot = null;
                    if (cEquipmentSlot != null) {
                        try {
                            equipmentSlot = EquipmentSlot.valueOf(cEquipmentSlot);
                        } catch (Exception ignored) {
                            // ignored
                        }
                    }
                    AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;
                    try {
                        operation = AttributeModifier.Operation.valueOf(cModifierOperation);
                    } catch (Exception ignored) {
                        // ignored
                    }

                    AttributeModifier modifier;
                    if (uuid == null) {
                        modifier = new AttributeModifier(cModifierName, cAmount, operation);
                    } else if (equipmentSlot == null) {
                        modifier = new AttributeModifier(uuid, cModifierName, cAmount, operation);
                    } else {
                        modifier = new AttributeModifier(uuid, cModifierName, cAmount, operation, equipmentSlot);
                    }

                    ism.addAttributeModifier(attribute, modifier);
                }
            }
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


        // unbreakable
        if (!filters.contains(Filter.UNBREAKABLE)) {
            ism.setUnbreakable(unbreakable);
        }

        // enchantments
        if (!filters.contains(Filter.ENCHANTMENTS)) {
            if (config.isSet(path + "enchantments")) {
                for (String key : config.getStringList(path + "enchantments")) {
                    String[] split = key.split(":");
                    if (split.length < 2) {
                        plugin.getQuestsLogger().debug("Enchantment does not follow format {namespace}:{name}:{level} : " + key);
                        continue;
                    }
                    String namespace = split[0];
                    String ench = split[1];
                    String levelName;
                    if (split.length >= 3) {
                        levelName = split[2];
                    } else {
                        levelName = "1";
                    }

                    NamespacedKey namespacedKey;
                    try {
                        namespacedKey = new NamespacedKey(namespace, ench);
                    } catch (Exception e) {
                        plugin.getQuestsLogger().debug("Unrecognised namespace: " + namespace);
                        continue;
                    }
                    Enchantment enchantment;
                    if ((enchantment = Enchantment.getByKey(namespacedKey)) == null) {
                        plugin.getQuestsLogger().debug("Unrecognised enchantment: " + namespacedKey);
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
        Material type;
        try {
            type = Material.valueOf(material);
        } catch (Exception e) {
            plugin.getQuestsLogger().debug("Unrecognised material: " + material);
            type = Material.STONE;
        }
        return new ItemStack(type, 1);
    }

    @Override
    public boolean isValidMaterial(String material) {
        try {
            Material.valueOf(material);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
