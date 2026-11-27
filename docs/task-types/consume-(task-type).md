---
title: consume
parent: Built-in task types
grand_parent: Task types
---

# consume (task type)

Since v3.9
{: .label .label-green }

Consume a specific item.

## Options

| Key              | Description                                            | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|------------------|--------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`         | The number of items to consume.                        | Integer                | Yes      | \-      | Must be at least 1. |
| `item-requirement` | Item requirement(s) for `item`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `item`. See [item requirements](../configuration/item-requirements). |
| `item`           | The specific item to obtain.                           | Material, or ItemStack | No       | \-      | **Deprecated:** will be removed in a future version, use `item-requirement` instead. Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `data`           | The data code for the item.                            | Integer                | No       | 0       | **Deprecated:** will be removed in a future version, use `damage` in `item-requirement` instead. This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions. Must be between 0 and 32767. |
| `exact-match`    | Whether the item should exactly match what is defined. | Boolean                | No       | true    | **Deprecated:** will be removed in a future version, use `item-requirement` instead. |
| `worlds`         | Worlds which should count towards the progress.        | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |
| `ignored-worlds` | Worlds which should not count towards the progress.    | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Consume 8 of `BEEF`:

``` yaml
beef:
  type: "consume"
  item: BEEF                            # name of item (can be id or minecraft name)
  amount: 8                             # amount of item consumed
  data: 0                               # (OPTIONAL) data code
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Consume 8 of a specific item:

``` yaml
beef:
  type: "consume"
  item:                                 # SPECIFIC item with name and lore
    name: "&cSpecial Beef"
    type: "RAW_BEEF"
    lore:
     - "&7This is a special type of beef"
  amount: 8                             # amount of item consumed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Consume 8 of [quest item](../configuration/defining-items#quest-items)
`special_beef`:

``` yaml
beef:
  type: "consume"
  item:                                 # USING quest-item
    quest-item: "special_beef"
  amount: 8                             # amount of item needed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
