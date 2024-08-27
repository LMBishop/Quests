---
title: smithing
parent: Built-in task types
grand_parent: Task types
---

# smithing (task type)

Since v3.13.2
{: .label .label-green }

Minecraft 1.16+ required
{: .label .label-purple }

Smith a certain number of items using a smithing table.

## Options

| Key           | Description                                            | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                                                                       |
|---------------|--------------------------------------------------------|------------------------|----------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`      | The number of items to smith.                          | Integer                | Yes      | \-      | \-                                                                                                                                                                                                                                                                                                                          |
| `item`        | The specific item to smith.                            | Material, or ItemStack | No       | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. If this is not specified, any item will count. |
| `exact-match` | Whether the item should exactly match what is defined. | Boolean                | No       | true    | \-                                                                                                                                                                                                                                                                                                                          |
| `mode`        | The specific mode of smithing.                         | String                 | 1.20+    | \-      | One of: `any`, `transform`, `trim`.                                                                                                                                                                                                                                                                                         |
| `worlds`      | Worlds which should count towards the progress.        | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                                                                          |

## Examples

Smith 10 items:

``` yaml
smithing:
  type: "smithing"
  amount: 10                            # amount to smith
  mode: "transform"                     # mode of smithing
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Smith a Netherite Chestplate:

``` yaml
smithing:
  type: "smithing"
  amount: 1                             # amount to smith
  item: NETHERITE_CHESTPLATE            # type of item 
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
