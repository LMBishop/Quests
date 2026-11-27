---
title: itemmending
parent: Built-in task types
grand_parent: Task types
---

# itemmending (task type)

Since v3.15.1
{: .label .label-green }

Mend certain items with a set amount of repair (by increasing durability with mending enchantment).

## Options

| Key              | Description                                            | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|------------------|--------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`         | The number of repair to mend the item.                 | Integer                | Yes      | \-      | Must be at least 1.                                                                                                                                                                                                                                                          |
| `item-requirement` | Item requirement(s) for `item`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `item`. See [item requirements](../configuration/item-requirements). |
| `item`           | The specific item to mend.                             | Material, or ItemStack | No       | \-      | **Deprecated:** will be removed in a future version, use `item-requirement` instead. Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `data`           | The data code for the item.                            | Integer                | No       | 0       | **Deprecated:** will be removed in a future version, use `damage` in `item-requirement` instead. This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions. Must be between 0 and 32767. |
| `exact-match`    | Whether the item should exactly match what is defined. | Boolean                | No       | true    | **Deprecated:** will be removed in a future version, use `item-requirement` instead. |
| `worlds`         | Worlds which should count towards the progress.        | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |
| `ignored-worlds` | Worlds which should not count towards the progress.    | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Mend diamond pickaxes with 12000 points of repair:

``` yaml
itemmending:
  type: "itemmending"
  amount: 12000           # amount of repair to mend the item
  item: DIAMOND_PICKAXE   # item to mend
  exact-match: false      # we need to ignore nbt
```
