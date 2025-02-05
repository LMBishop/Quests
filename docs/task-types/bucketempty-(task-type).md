---
title: bucketempty
parent: Built-in task types
grand_parent: Task types
---

# bucketempty (task type)

Since v3.9
{: .label .label-green }

Empty a bucket.

## Options

| Key           | Description                                            | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|---------------|--------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`      | The number of buckets to empty.                        | Integer                | Yes      | \-      | \-                                                                                                                                                                                                                                                                           |
| `bucket`      | The specific bucket to capture.                        | Material, or ItemStack | No       | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `data`        | The data code for the item.                            | Integer                | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions.                                                                                                                                                                         |
| `exact-match` | Whether the item should exactly match what is defined. | Boolean                | No       | true    | \-                                                                                                                                                                                                                                                                           |
| `worlds`      | Worlds which should count towards the progress.        | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Empty 8 lava buckets:

``` yaml
bucketempty :
  type: "bucketempty"
  bucket: LAVA_BUCKET                   # bucket to empty
  amount: 8                             # amount of times to empty
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
