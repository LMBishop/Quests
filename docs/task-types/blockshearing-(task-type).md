---
title: blockshearing
parent: Built-in task types
grand_parent: Task types
---

# blockshearing (task type)

Since v3.13.3
{: .label .label-green }

Minecraft 1.16.4+ required
{: .label .label-purple }

Paper required
{: .label .label-yellow }

Shear a set amount of blocks.

## Options

| Key            | Description                                                        | Type                          | Required | Default | Notes                                                                                                                                                                                                                                                                                                                   |
|----------------|--------------------------------------------------------------------|-------------------------------|----------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`       | The number of blocks to shear.                                     | Integer                       | Yes      | \-      | \-                                                                                                                                                                                                                                                                                                                      |
| `block`        | The specific block(s) to shear.                                    | Material, or list of material | No       | \-      | Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. Note that some items are confusingly named, they may refer to the held item or block instead of the crop block. |
| `item`         | The specific item to be dropped when shearing.                     | Material, or ItemStack        | No       | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names.                                            |
| `data`         | The data code for the item.                                        | Integer                       | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions.                                                                                                                                                                                                                    |
| `exact-match`  | Whether the item should exactly match what is defined.             | Boolean                       | No       | true    | \-                                                                                                                                                                                                                                                                                                                      |
| `count-shears` | Whether the plugin should count shears (actions) instead of items. | Boolean                       | No       | true    | \-                                                                                                                                                                                                                                                                                                                      |
| `worlds`       | Worlds which should count towards the progress.                    | List of world names           | No       | \-      | \-                                                                                                                                                                                                                                                                                                                      |

## Examples

Carve a pumpkin:

``` yaml
blockshearing:
  type: "blockshearing"
  amount: 1                             # amount of blocks to be broken
  block: "PUMPKIN"                      # name of block
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
