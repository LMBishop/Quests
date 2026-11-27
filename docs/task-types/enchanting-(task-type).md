---
title: enchanting
parent: Built-in task types
grand_parent: Task types
---

# enchanting (task type)

Since v2.2
{: .label .label-green }

Enchant an item.

## Options

| Key              | Description                                         | Type                                 | Required | Default | Notes                                                                                                                                                                                                                                                                                                                                 |
|------------------|-----------------------------------------------------|--------------------------------------|----------|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`         | The number of items to obtain.                      | Integer                              | Yes      | \-      | Must be at least 1.                                                                                                                                                                                                                                                                                                                   |
| `item-requirement` | Item requirement(s) for `item`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `item`. See [item requirements](../configuration/item-requirements). |
| `item`           | The specific item to enchant.                       | Material, or ItemStack               | No       | \-      | **Deprecated:** will be removed in a future version, use `item-requirement` instead. Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. If no item is specified, then any item can be enchanted. |
| `enchantment`    | The specific enchantment(s) to apply.               | Enchantment, or list of enchantments | No       | \-      | If no enchantments are specified, then any enchantment can be applied.                                                                                                                                                                                                                                                                |
| `min-level`      | The minimum level of the enchantments.              | Integer                              | No       | \-      | If no minimum level is specified, then any enchantment of any level can be applied. Must be at least 1.                                                                                                                                                                                                                               |
| `worlds`         | Worlds which should count towards the progress.     | List of world names                  | No       | \-      | \-                                                                                                                                                                                                                                                                                                                                    |
| `ignored-worlds` | Worlds which should not count towards the progress. | List of world names                  | No       | \-      | \-                                                                                                                                                                                                                                                                                                                                    |

## Examples

Enchant 10 items:

``` yaml
enchanting:
  type: "enchanting"
  amount: 10                            # amount of items enchanted
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Enchant 10 items with Protection:

``` yaml
beef:
  type: "enchanting"
  amount: 10                            # amount of items 
  enchantment: PROTECTION_ENVIRONMENTAL # (OPTIONAL) enchantment to apply
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Enchant 10 items with Protection IV:

``` yaml
beef:
  type: "enchanting"
  amount: 10                            # amount of items 
  enchantment: PROTECTION_ENVIRONMENTAL # (OPTIONAL) enchantment to apply
  min-level: 4                          # (OPTIONAL) the minimum level of enchantment to apply
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
