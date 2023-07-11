---
title: bucketfill
parent: Built-in task types
grand_parent: Task types
---

# bucketfill (task type)

Since v3.9
{: .label .label-green }

Fill a bucket up.

## Options

| Key      | Description                                     | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|----------|-------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount` | The number of buckets to fill.                  | Integer                | Yes      | \-      | \-                                                                                                                                                                                                                                                                           |
| `item`   | The specific bucket to fill.                    | Material, or ItemStack | Yes      | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `worlds` | Worlds which should count towards the progress. | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Fill 8 lava buckets:

``` yaml
bucketfill:
  type: "bucketfill"
  bucket: LAVA_BUCKET                   # bucket to fill
  amount: 8                             # amount of times to fill
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
