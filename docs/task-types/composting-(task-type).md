---
title: composting
parent: Built-in task types
grand_parent: Task types
---

# composting (task type)

Since v3.15.1
{: .label .label-green }

Compost a specific item.

## Options

| Key           | Description                                            | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|---------------|--------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`      | The number of items to compost.                        | Integer                | Yes      | \-      | \-                                                                                                                                                                                                                                                                           |
| `item`        | The specific item to compost.                          | Material, or ItemStack | No       | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `data`        | The data code for the item.                            | Integer                | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions.                                                                                                                                                                         |
| `exact-match` | Whether the item should exactly match what is defined. | Boolean                | No       | true    | \-                                                                                                                                                                                                                                                                           |
| `worlds`      | Worlds which should count towards the progress.        | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Compost 10 carrots:

``` yaml
compostcarrots:
  type: "composting"
  amount: 8                             # amount of items to compost
  item: CARROT                          # (OPTIONAL) item to compost
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
