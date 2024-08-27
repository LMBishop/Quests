---
title: crafting
parent: Built-in task types
grand_parent: Task types
---

# crafting (task type)

Since v3.0
{: .label .label-green }

Craft a set of items.

## Options

| Key           | Description                                            | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|---------------|--------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`      | The number of items to craft.                          | Integer                | Yes      | \-      | \-                                                                                                                                                                                                                                                                           |
| `item`        | The specific item to craft.                            | Material, or ItemStack | Yes      | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `data`        | The data code for the item.                            | Integer                | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions.                                                                                                                                                                         |
| `exact-match` | Whether the item should exactly match what is defined. | Boolean                | No       | true    | \-                                                                                                                                                                                                                                                                           |
| `worlds`      | Worlds which should count towards the progress.        | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Craft 5 of `COMPASS`:

``` yaml
compass:
  type: "crafting"
  item: COMPASS                         # name of item (can be id or minecraft name)
  amount: 5                             # amount of item needed
  data: 0                               # (OPTIONAL) data code
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Craft 5 of a specific item:

``` yaml
compass:
  type: "crafting"
  item:                                 # SPECIFIC item with name and lore
    name: "&cSuper Compass"
    type: "COMPASS"
    lore:
     - "&7This is special compass with a fancy name"
  amount: 5                             # amount of item needed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Craft 5 of [quest item](../configuration/defining-items#quest-items)
`super_compass`:

``` yaml
compass:
  type: "crafting"
  item:                                 # USING quest-item
    quest-item: "super_compass"
  amount: 5                             # amount of item needed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
