---
title: itembreaking
parent: Built-in task types
grand_parent: Task types
---

# itembreaking (task type)

Not released yet (dev builds)
{: .label .label-green }

Break a set amount of certain items (by reducing durability to 0).

## Options

| Key           | Description                                            | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                       |
|---------------|--------------------------------------------------------|------------------------|----------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`      | The number of items to break.                          | Integer                | Yes      | \-      | \-                                                                                                                                                                                                                                                          |
| `item`        | The specific item to break.                            | Material, or ItemStack | No       | \-      | Accepts standard [item definition](defining_items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `data`        | The data code for the item.                            | Integer                | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions.                                                                                                                                                        |
| `exact-match` | Whether the item should exactly match what is defined. | Boolean                | No       | true    | \-                                                                                                                                                                                                                                                          |
| `worlds`      | Worlds which should count towards the progress.        | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                          |

## Examples

Break 8 diamond pickaxes:

``` yaml
itembreaking:
  type: "itembreaking"
  amount: 8               # amount of items to break
  item: DIAMOND_PICKAXE   # item to break
  exact-match: false      # we need to ignore nbt
```
