---
title: smelting
parent: Built-in task types
grand_parent: Task types
---

# smelting (task type)

Since v3.12
{: .label .label-green }

Cook a set amount of an item.

{: .note }
Since Quests v3.13, `smeltingcertain` and `smelting` have been merged
into one. Both names can be used to refer to this task.

## Options

| Key              | Description                                            | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                                                                       |
|------------------|--------------------------------------------------------|------------------------|----------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`         | The number of items to smelt.                          | Integer                | Yes      | \-      | Must be at least 1.                                                                                                                                                                                                                                                                                                         |
| `item-requirement` | Item requirement(s) for `item`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `item`. See [item requirements](../configuration/item-requirements). |
| `item`           | The specific item to smelt.                            | Material, or ItemStack | No       | \-      | **Deprecated:** will be removed in a future version, use `item-requirement` instead. Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. If this is not specified, any item will count. |
| `data`           | The data code for the item.                            | Integer                | No       | 0       | **Deprecated:** will be removed in a future version, use `damage` in `item-requirement` instead. This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions. Must be between 0 and 32767. |
| `exact-match`    | Whether the item should exactly match what is defined. | Boolean                | No       | true    | **Deprecated:** will be removed in a future version, use `item-requirement` instead. |
| `mode`           | The specific mode of smelting.                         | String                 | No       | \-      | One of: `smoker`, `blast_furnace`, `furnace`. If this is not specified, any furnace will count.                                                                                                                                                                                                                             |
| `worlds`         | Worlds which should count towards the progress.        | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                                                                          |
| `ignored-worlds` | Worlds which should not count towards the progress.    | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                                                                          |

## Examples

Smelt 10 items:

``` yaml
smelting:
  type: "smelting"
  amount: 10                            # amount to smelt
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Cook 10 steak:

``` yaml
smeltingcertain:
  type: "smeltingcertain"
  amount: 10                            # amount to smelt
  item: STEAK                           # type of item 
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Cook 10 of a specific item:

``` yaml
smeltingcertain:
  type: "smeltingcertain"
  amount: 10                            # amount to smelt
  item:                                 # SPECIFIC item with name and lore
    name: "&cSpecial Beef"
    type: "RAW_BEEF"
    lore:
     - "&7This is a special type of beef"
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Cook 10 of [quest item](../configuration/defining-items#quest-items)
`special_beef`:

``` yaml
smeltingcertain:
  type: "smeltingcertain"
  amount: 10                            # amount to smelt
  item:                                 # type of item 
    quest-item: "special_beef"
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
