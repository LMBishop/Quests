---
title: farming
parent: Built-in task types
grand_parent: Task types
---

# farming (task type)

Since v3.5
{: .label .label-green }

Minecraft 1.13+ required
{: .label .label-purple }

*For previous versions, use [blockbreak](blockbreak-(task-type)).*

Farm a set amount of crops.

{: .note }
Since Quests v3.13, `farmingcertain` and `farming` have been merged into
one. Both names can be used to refer to this task.

## Options

| Key                        | Description                                                                                    | Type                          | Required | Default | Notes                                                                                                                                                                                                                                                                                                                   |
|----------------------------|------------------------------------------------------------------------------------------------|-------------------------------|----------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`                   | The number of crops to farm.                                                                   | Integer                       | Yes      | \-      | \-                                                                                                                                                                                                                                                                                                                      |
| `block`                    | The specific crop(s) to farm.                                                                  | Material, or list of material | No       | \-      | Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. Note that some items are confusingly named, they may refer to the held item or block instead of the crop block. |
| `check-coreprotect`        | Whether the plugin should query CoreProtect if a block has been recently placed.               | Boolean                       | No       | false   | This requires the CoreProtect plugin.                                                                                                                                                                                                                                                                                   |
| `check-coreprotect-time`   | How long ago (in seconds) the plugin should check with CoreProtect whether a block was placed. | Integer                       | No       | 3600    | Used with `check-coreprotect`.                                                                                                                                                                                                                                                                                          |
| `check-playerblocktracker` | Whether the plugin should query PlayerBlockTracker if a block has been recently placed.        | Boolean                       | No       | false   | This requires the PlayerBlockTracker plugin.                                                                                                                                                                                                                                                                            |
| `mode`                     | The mode to harvest crops.                                                                     | String                        | No       | \-      | One of: `break`, `harvest`. A harvest is where a block drops an item (usually a crop) but does not change state. If this is not specified, both will be accepted.                                                                                                                                                       |
| `worlds`                   | Worlds which should count towards the progress.                                                | List of world names           | No       | \-      | \-                                                                                                                                                                                                                                                                                                                      |

## Examples

Farm 10 crops:

``` yaml
farming:
  type: "farming"
  amount: 10                            # amount of blocks to be broken
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Farm 10 wheat:

``` yaml
farming:
  type: "farming"
  amount: 10                            # amount of blocks to be brkoen
  block: WHEAT                          # name of block (can be id or minecraft name)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Farm 10 wheat or beetroot:

``` yaml
farmingmultiple:
  type: "farming"
  amount: 10                            # amount of blocks to be placed
  blocks:                               # name of blocks which will count towards progress
   - WHEAT
   - BEETROOTS                          
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
