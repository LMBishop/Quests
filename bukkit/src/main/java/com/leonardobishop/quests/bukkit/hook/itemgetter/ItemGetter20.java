package com.leonardobishop.quests.bukkit.hook.itemgetter;

import com.google.common.collect.ImmutableMultimap;
import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.util.CompatUtils;
import com.leonardobishop.quests.bukkit.util.NamespacedKeyUtils;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Reads the following:
 * <ul>
 *     <li>type (<b>without</b> data support, <b>with</b> namespace support)</li>
 *     <li>name</li>
 *     <li>lore</li>
 *     <li>enchantments (<b>with</b> namespace support)</li>
 *     <li>item flags</li>
 *     <li>unbreakability (<b>with</b> CraftBukkit support)</li>
 *     <li>attribute modifiers</li>
 *     <li>custom model data</li>
 *     <li>attribute modifiers (<b>without</b> namespace support)</li> // TODO: add namespace support
 *     <li>hide tooltip</li>
 * </ul>
 * Requires at least API version 1.20.(5).
 */
@SuppressWarnings("DuplicatedCode")
public class ItemGetter20 extends ItemGetter {

    // Introduced in 1.21.4 (technically, a new item getter could be created, but it doesn't really make sense as the check would still be needed)
    private static final boolean V1_21_4_OR_HIGHER = CompatUtils.classWithMethodExists("org.bukkit.inventory.meta.ItemMeta", "setItemModel", NamespacedKey.class);

    public ItemGetter20(BukkitQuestsPlugin plugin) {
        super(plugin);
    }

    @SuppressWarnings({"UnstableApiUsage", "removal", "deprecation"})
    @Override
    public ItemStack getItem(String path, ConfigurationSection config, Filter... excludes) {
        config = config.getConfigurationSection(path);
        if (config == null) {
            return INVALID_ITEM_STACK;
        }

        Set<Filter> filters = Set.of(excludes);

        // type (without data)
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

                boolean namespaced = parts.length >= 2 && parts[0].startsWith("(") && parts[1].endsWith(")");

                Enchantment enchantment;
                if (namespaced) {
                    String namespacedKeyString = enchantmentString.substring(1, parts[0].length() + parts[1].length());
                    NamespacedKey namespacedKey = NamespacedKeyUtils.fromString(namespacedKeyString);
                    enchantment = Enchantment.getByKey(namespacedKey);
                } else {
                    enchantment = Enchantment.getByName(parts[0]);
                }

                if (enchantment == null) {
                    continue;
                }

                // (namespace:key):level
                // 0          1    2
                // SOME_ENUM_NAME:level
                // 0              1
                int levelIndex = namespaced ? 2 : 1;

                int level = 1;
                if (parts.length >= levelIndex + 1) {
                    try {
                        level = Integer.parseUnsignedInt(parts[levelIndex]);
                    } catch (NumberFormatException ignored) {
                    }
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

                if (itemFlag == ItemFlag.HIDE_ATTRIBUTES) {
                    // I don't like the solution due to the loss of the original attributes,
                    // however ItemGetters are used only for GUIs so it's not a real loss.
                    meta.setAttributeModifiers(ImmutableMultimap.of());
                }

                meta.addItemFlags(itemFlag);
            }
        }

        // unbreakability
        Boolean unbreakable = (Boolean) config.get("unbreakable");
        if (unbreakable != null && !filters.contains(Filter.UNBREAKABLE)) {
            meta.setUnbreakable(unbreakable);
        }

        // attribute modifiers // TODO: use dedicated 1.20.5 solution
        List<Map<?, ?>> attributeModifierMaps = config.getMapList("attributemodifiers");
        if (!attributeModifierMaps.isEmpty() && !filters.contains(Filter.ATTRIBUTE_MODIFIER)) {
            for (Map<?, ?> attributeModifierMap : attributeModifierMaps) {
                // attribute
                String attributeString = (String) attributeModifierMap.get("attribute");
                Attribute attribute;
                try {
                    attribute = Attribute.valueOf(attributeString);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                // modifier (map)
                Map<?, ?> modifierMap = (Map<?, ?>) attributeModifierMap.get("modifier");
                if (modifierMap == null) {
                    continue;
                }

                // modifier unique id
                String modifierUniqueIdString = (String) modifierMap.get("uuid");
                UUID modifierUniqueId;
                try {
                    modifierUniqueId = UUID.fromString(modifierUniqueIdString);
                } catch (IllegalArgumentException e) {
                    modifierUniqueId = null;
                }

                // modifier name
                String modifierName = (String) modifierMap.get("name");
                if (modifierName == null) {
                    continue;
                }

                // modifier amount
                Object modifierAmountObject = modifierMap.get("amount");
                double modifierAmount;
                if (modifierAmountObject instanceof Number modifierAmountNumber) {
                    modifierAmount = modifierAmountNumber.doubleValue();
                } else {
                    continue;
                }

                // modifier operation
                String modifierOperationString = (String) modifierMap.get("operation");
                AttributeModifier.Operation modifierOperation;
                try {
                    modifierOperation = AttributeModifier.Operation.valueOf(modifierOperationString);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                // modifier equipment slot
                String equipmentSlotString = (String) modifierMap.get("equipmentslot");
                EquipmentSlot equipmentSlot;
                try {
                    equipmentSlot = EquipmentSlot.valueOf(equipmentSlotString);
                } catch (IllegalArgumentException e) {
                    equipmentSlot = null;
                }

                // modifier (ctor)
                AttributeModifier modifier;
                if (modifierUniqueId != null) {
                    if (equipmentSlot != null) {
                        modifier = new AttributeModifier(modifierUniqueId, modifierName, modifierAmount, modifierOperation, equipmentSlot);
                    } else {
                        modifier = new AttributeModifier(modifierUniqueId, modifierName, modifierAmount, modifierOperation);
                    }
                } else {
                    modifier = new AttributeModifier(modifierName, modifierAmount, modifierOperation);
                }

                meta.addAttributeModifier(attribute, modifier);
            }
        }

        // custom model data
        Integer customModelData = (Integer) config.get("custommodeldata");
        if (customModelData != null && !filters.contains(Filter.CUSTOM_MODEL_DATA)) {
            meta.setCustomModelData(customModelData);
        }

        if (V1_21_4_OR_HIGHER) {
            // item model
            String itemModelString = (String) config.get("itemmodel");
            if (itemModelString != null && !filters.contains(Filter.ITEM_MODEL)) {
                NamespacedKey itemModel = NamespacedKeyUtils.fromString(itemModelString);

                if (itemModel != null) {
                    meta.setItemModel(itemModel);
                }
            }

            // custom model data colors
            List<Integer> argbs = config.getIntegerList("custommodeldata-colors");
            if (!argbs.isEmpty() && !filters.contains(Filter.CUSTOM_MODEL_DATA)) {
                List<Color> colors = new ArrayList<>(argbs.size());

                for (Integer argb : argbs) {
                    if (argb == null) {
                        continue; // probably impossible
                    }

                    Color color = Color.fromARGB(argb);
                    colors.add(color);
                }

                final CustomModelDataComponent customModelDataComponent = meta.getCustomModelDataComponent();
                customModelDataComponent.setColors(colors);
                meta.setCustomModelDataComponent(customModelDataComponent);
            }

            // custom model data flags
            List<Boolean> flags = config.getBooleanList("custommodeldata-flags");
            if (!flags.isEmpty() && !filters.contains(Filter.CUSTOM_MODEL_DATA)) {
                final CustomModelDataComponent customModelDataComponent = meta.getCustomModelDataComponent();
                customModelDataComponent.setFlags(flags);
                meta.setCustomModelDataComponent(customModelDataComponent);
            }

            // custom model data floats
            List<Float> floats = config.getFloatList("custommodeldata-floats");
            if (!floats.isEmpty() && !filters.contains(Filter.CUSTOM_MODEL_DATA)) {
                final CustomModelDataComponent customModelDataComponent = meta.getCustomModelDataComponent();
                customModelDataComponent.setFloats(floats);
                meta.setCustomModelDataComponent(customModelDataComponent);
            }

            // custom model data strings
            List<String> strings = config.getStringList("custommodeldata-strings");
            if (!strings.isEmpty() && !filters.contains(Filter.CUSTOM_MODEL_DATA)) {
                final CustomModelDataComponent customModelDataComponent = meta.getCustomModelDataComponent();
                customModelDataComponent.setStrings(strings);
                meta.setCustomModelDataComponent(customModelDataComponent);
            }
        }

        // enchantment glint override
        Boolean enchantmentGlintOverride = (Boolean) config.get("enchantmentglintoverride");
        if (enchantmentGlintOverride != null && !filters.contains(Filter.ENCHANTMENT_GLINT_OVERRIDE)) {
            meta.setEnchantmentGlintOverride(enchantmentGlintOverride);
        }

        // hide tooltip
        Boolean hideTooltip = (Boolean) config.get("hidetooltip");
        if (hideTooltip != null && !filters.contains(Filter.HIDE_TOOLTIP)) {
            meta.setHideTooltip(hideTooltip);
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

        NamespacedKey typeKey = NamespacedKeyUtils.fromString(typeString);
        if (typeKey == null) {
            return INVALID_ITEM_STACK;
        }

        type = Registry.MATERIAL.get(typeKey);
        if (type != null) {
            return new ItemStack(type, 1);
        }

        return INVALID_ITEM_STACK;
    }

    @Override
    public boolean isValidMaterial(String typeString) {
        if (Material.getMaterial(typeString) != null) {
            return true;
        }

        NamespacedKey typeKey = NamespacedKeyUtils.fromString(typeString);
        if (typeKey == null) {
            return false;
        }

        return Registry.MATERIAL.get(typeKey) != null;
    }
}
