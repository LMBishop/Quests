---
title: bucketentity
parent: Built-in task types
grand_parent: Task types
---

# bucketentity (task type)

Since v3.15.1
{: .label .label-green }

Capture specific entity in a bucket.

## Options

| Key           | Description                                            | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|---------------|--------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`      | The number of buckets to capture an entity in.         | Integer                | Yes      | \-      | \-                                                                                                                                                                                                                                                                           |
| `bucket`      | The specific bucket to capture.                        | Material, or ItemStack | No       | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `data`        | The data code for the item.                            | Integer                | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions.                                                                                                                                                                         |
| `exact-match` | Whether the item should exactly match what is defined. | Boolean                | No       | true    | \-                                                                                                                                                                                                                                                                           |
| `worlds`      | Worlds which should count towards the progress.        | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Capture 8 axolotls in buckets:

``` yaml
bucketentity:
  type: "bucketentity"
  bucket: AXOLOTL_BUCKET                # (OPTIONAL) bucket to capture the entity
  exact-match: false                    # (OPTIONAL) ignore the axolotl variation nbt
  amount: 8                             # amount of times to capture entity
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
