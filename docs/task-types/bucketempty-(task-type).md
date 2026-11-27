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

| Key              | Description                                            | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|------------------|--------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`         | The number of buckets to empty.                        | Integer                | Yes      | \-      | Must be at least 1.                                                                                                                                                                                                                                                          |
| `bucket-requirement` | Item requirement(s) for `bucket`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `bucket`. See [item requirements](../configuration/item-requirements). |
| `bucket`         | The specific bucket to capture.                        | Material, or ItemStack | No       | \-      | **Deprecated:** will be removed in a future version, use `bucket-requirement` instead. Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `data`           | The data code for the item.                            | Integer                | No       | 0       | **Deprecated:** will be removed in a future version, use `damage` in `bucket-requirement` instead. This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions. Must be between 0 and 32767. |
| `exact-match`    | Whether the item should exactly match what is defined. | Boolean                | No       | true    | **Deprecated:** will be removed in a future version, use `bucket-requirement` instead. |
| `worlds`         | Worlds which should count towards the progress.        | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |
| `ignored-worlds` | Worlds which should not count towards the progress.    | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

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
