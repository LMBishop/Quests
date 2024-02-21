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

| Key           | Description                                     | Type                                 | Required | Default | Notes                                                                                                                                                                                                                                                                                                                                 |
|---------------|-------------------------------------------------|--------------------------------------|----------|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`      | The number of items to obtain.                  | Integer                              | Yes      | \-      | \-                                                                                                                                                                                                                                                                                                                                    |
| `item`        | The specific item to enchant.                   | Material, or ItemStack               | No       | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. If no item is specified, then any item can be enchanted. |
| `enchantment` | The specific enchantment(s) to apply.           | Enchantment, or list of enchantments | No       | \-      | If no enchantments are specified, then any enchantment can be applied.                                                                                                                                                                                                                                                                |
| `min-level`   | The minimum level of the enchantments.          | Integer                              | No       | \-      | If no minimum level is specified, then any enchantment of any level can be applied.                                                                                                                                                                                                                                                   |
| `worlds`      | Worlds which should count towards the progress. | List of world names                  | No       | \-      | \-                                                                                                                                                                                                                                                                                                                                    |

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
