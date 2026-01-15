---
title: blockbreak
parent: Built-in task types
grand_parent: Task types
---

# blockbreak (task type)

Since v1.0
{: .label .label-green }

Break a set amount of blocks. For items which can be broken by
breaking a block underneath it (such as torches or signs), consider
using the [blockitemdropping task type](blockitemdropping-(task-type))
instead.

{: .note }
Since Quests v3.13, `blockbreakcertain` and `blockbreak` have been
merged into one. Both names can be used to refer to this task.

## Options

| Key                        | Description                                                                                    | Type                           | Required | Default | Notes                                                                                                                                                                                                                                                                           |
|----------------------------|------------------------------------------------------------------------------------------------|--------------------------------|----------|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`                   | The number of blocks to break.                                                                 | Integer                        | Yes      | \-      | \-                                                                                                                                                                                                                                                                              |
| `block`                    | The specific blocks to break.                                                                  | Material, or list of materials | No       | \-      | Not specifying this field will allow all blocks to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for block names. |
| `data`                     | The data code for the block.                                                                   | Integer                        | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with lists of blocks.                                                                                                                                                                                  |
| `reverse-if-placed`        | Whether placing blocks should decrement from the quest progress.                               | Boolean                        | No       | false   | This allows negative task progress unless `allow-negative-progress` is set to `false`.                                                                                                                                                                                          |
| `check-coreprotect`        | Whether the plugin should query CoreProtect if a block has been recently placed.               | Boolean                        | No       | false   | This requires the CoreProtect plugin.                                                                                                                                                                                                                                           |
| `check-coreprotect-time`   | How long ago (in seconds) the plugin should check with CoreProtect whether a block was placed. | Integer                        | No       | 3600    | Used with `check-coreprotect`.                                                                                                                                                                                                                                                  |
| `check-playerblocktracker` | Whether the plugin should query PlayerBlockTracker if a block has been recently placed.        | Boolean                        | No       | false   | This requires the PlayerBlockTracker plugin.                                                                                                                                                                                                                                    |
| `allow-silk-touch`         | Whether mining a block with a silk touch pickaxe should count.                                 | Boolean                        | No       | true    | \-                                                                                                                                                                                                                                                                              |
| `allow-negative-progress`  | Whether progress can be allowed to enter the negatives.                                        | Boolean                        | No       | true    | Used with `reverse-if-placed`.                                                                                                                                                                                                                                                  |
| `worlds`                   | Worlds which should count towards the progress.                                                | List of world names            | No       | \-      | \-                                                                                                                                                                                                                                                                              |

## Examples

Break 10 of any block:

``` yaml
mining:
  type: "blockbreak"
  amount: 10                            # amount of blocks to be broken
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Break 10 of stone:

``` yaml
miningstone:
  type: "blockbreakcertain"
  amount: 10                            # amount of blocks to be broken
  block: STONE                          # name of block (can be id or minecraft name)
  data: 1                               # (OPTIONAL) data code 
  reverse-if-placed: false              # (OPTIONAL) if true, blocks of same type placed will reverse progression (prevents silk-touch exploit)
  check-coreprotect: false              # (OPTIONAL) if true and CoreProtect is present, the plugin will check its logs for player placed blocks
  check-coreprotect-time: 3600          # (OPTIONAL) time in seconds for the maximum logging period to check
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Break 10 of either stone or gold ore:

``` yaml
miningmultiple:
  type: "blockbreakcertain"
  amount: 10                            # amount of blocks to be broken
  blocks:                               # name of blocks which will count towards progress
   - STONE
   - GOLD_ORE                           
  reverse-if-placed: false              # (OPTIONAL) if true, blocks of same type placed will reverse progression (prevents silk-touch exploit)
  check-coreprotect: false              # (OPTIONAL) if true and CoreProtect is present, the plugin will check its logs for player placed blocks
  check-coreprotect-time: 3600          # (OPTIONAL) time in seconds for the maximum logging period to check
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
