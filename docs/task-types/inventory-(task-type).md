---
title: inventory
parent: Built-in task types
grand_parent: Task types
---

# inventory (task type)

Since v1.4
{: .label .label-green }


Obtain a set of items.

## Options

| Key                          | Description                                                      | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|------------------------------|------------------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`                     | The number of items to obtain.                                   | Integer                | Yes      | \-      | Must be at least 1.                                                                                                                                                                                                                                                          |
| `item-requirement` | Item requirement(s) for `item`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `item`. See [item requirements](../configuration/item-requirements). Either `item` or `item-requirement` must be set. |
| `item`                       | The specific item to obtain.                                     | Material, or ItemStack | No | \-      | **Deprecated:** will be removed in a future version, use `item-requirement` instead. Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. Either `item` or `item-requirement` must be set. |
| `data`                       | The data code for the item.                                      | Integer                | No       | 0       | **Deprecated:** will be removed in a future version, use `damage` in `item-requirement` instead. This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions. Must be between 0 and 32767. |
| `exact-match`                | Whether the item should exactly match what is defined.           | Boolean                | No       | true    | **Deprecated:** will be removed in a future version, use `item-requirement` instead. |
| `remove-items-when-complete` | Whether the items should be taken from the player when complete. | Boolean                | No       | false   | If `allow-partial-completion` is true, then this will also be set to true.                                                                                                                                                                                                   |
| `allow-partial-completion`   | Whether any number of items counts towards the task progress.    | Boolean                | No       | true    | Setting to true will force `remove-items-when-complete` to true as well. If a player obtains any matching item, it will be immediately taken away from them and added towards the quest progress.                                                                            |
| `worlds`                     | Worlds which should count towards the progress.                  | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |
| `ignored-worlds`             | Worlds which should not count towards the progress.              | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Obtain 8 of `BEEF`:

``` yaml
beef:
  type: "inventory"
  item: BEEF                           # name of item (can be id or minecraft name)
  amount: 8                             # amount of item needed
  data: 0                               # (OPTIONAL) data code
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  allow-partial-completion: false       # (OPTIONAL) allow partial deliveries - default: true
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Obtain 8 of a specific item:

``` yaml
beef:
  type: "inventory"
  item:                                 # SPECIFIC item with name and lore
    name: "&cSpecial Beef"
    type: "RAW_BEEF"
    lore:
     - "&7This is a special type of beef"
  amount: 8                             # amount of item needed
  data: 0                               # (OPTIONAL) data code
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  allow-partial-completion: false       # (OPTIONAL) allow partial deliveries - default: true
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Obtain 8 of [quest item](../configuration/defining-items#quest-items)
`special_beef`:

``` yaml
beef:
  type: "inventory"
  item:                                 # USING quest-item
    quest-item: "specialbeef"
  amount: 8                             # amount of item needed
  data: 0                               # (OPTIONAL) data code
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  allow-partial-completion: false       # (OPTIONAL) allow partial deliveries - default: true
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
