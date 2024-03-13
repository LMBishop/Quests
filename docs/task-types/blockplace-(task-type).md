---
title: blockplace
parent: Built-in task types
grand_parent: Task types
---

# blockplace (task type)

Since v1.0
{: .label .label-green}

Place a set amount of blocks.

{: .note }
Since Quests v3.13, `blockplacecertain` and `blockplace` have been
merged into one. Both names can be used to refer to this task.

## Options

| Key                       | Description                                                       | Type                           | Required | Default | Notes                                                                                                                                                                                                                                                                           |
|---------------------------|-------------------------------------------------------------------|--------------------------------|----------|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`                  | The number of blocks to place.                                    | Integer                        | Yes      | \-      | \-                                                                                                                                                                                                                                                                              |
| `block`                   | The specific blocks to place.                                     | Material, or list of materials | No       | \-      | Not specifying this field will allow all blocks to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for block names. |
| `data`                    | The data code for the block.                                      | Integer                        | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with lists of blocks.                                                                                                                                                                                  |
| `reverse-if-broken`       | Whether breaking blocks should decrement from the quest progress. | Boolean                        | No       | false   | This allows negative task progress unless `allow-negative-progress` is set to `false`.                                                                                                                                                                                          |
| `allow-negative-progress` | Whether progress can be allowed to enter the negatives.           | Boolean                        | No       | true    | Used with `reverse-if-broken`.                                                                                                                                                                                                                                                  |
| `worlds`                  | Worlds which should count towards the progress.                   | List of world names            | No       | \-      | \-                                                                                                                                                                                                                                                                              |

## Examples

Place 10 of any block:

``` yaml
building:
  type: "blockplace"
  amount: 10                            # amount of blocks to be placed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Place 10 of stone:

``` yaml
buildingstone:
  type: "blockplace"
  amount: 10                            # amount of blocks to be placed
  block: STONE                          # name of block (minecraft name)
  data: 1                               # (OPTIONAL) data code
  reverse-if-broken: false              # (OPTIONAL) if true, blocks of same type broken will reverse progression (prevents silk-touch exploit)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Place 10 of either stone or gold ore:

``` yaml
buildingmultiple:
  type: "blockplace"
  amount: 10                            # amount of blocks to be placed
  blocks:                               # name of blocks which will count towards progress
   - STONE
   - GOLD_ORE                           
  reverse-if-broken: false              # (OPTIONAL) if true, blocks of same type broken will reverse progression (prevents silk-touch exploit)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
