---
title: blockchanging
parent: Built-in task types
grand_parent: Task types
---

# blockchanging (task type)

Not released yet (dev builds)
{: .label .label-green }

Minecraft 1.19+ required
{: .label .label-purple }

Change a set amount of blocks.

## Options

| Key                        | Description                                                                                    | Type                          | Required | Default | Notes                                                                                                                                                                                                                                                                                                                   |
|----------------------------|------------------------------------------------------------------------------------------------|-------------------------------|----------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`                   | The number of blocks to change.                                                                | Integer                       | Yes      | \-      | \-                                                                                                                                                                                                                                                                                                                      |
| `from` / `froms`           | The specific block(s) to change from.                                                          | Material, or list of material | No       | \-      | Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. Note that some items are confusingly named, they may refer to the held item or block instead of the crop block. |
| `to` / `tos`               | The specific block(s) to change to.                                                            | Material, or list of material | No       | \-      | Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. Note that some items are confusingly named, they may refer to the held item or block instead of the crop block. |
| `check-coreprotect`        | Whether the plugin should query CoreProtect if a block has been recently placed.               | Boolean                       | No       | false   | This requires the CoreProtect plugin.                                                                                                                                                                                                                                                                                   |
| `check-coreprotect-time`   | How long ago (in seconds) the plugin should check with CoreProtect whether a block was placed. | Integer                       | No       | 3600    | Used with `check-coreprotect`.                                                                                                                                                                                                                                                                                          |
| `check-playerblocktracker` | Whether the plugin should query PlayerBlockTracker if a block has been recently placed.        | Boolean                       | No       | false   | This requires the PlayerBlockTracker plugin.                                                                                                                                                                                                                                                                            |
| `worlds`                   | Worlds which should count towards the progress.                                                | List of world names           | No       | \-      | \-                                                                                                                                                                                                                                                                                                                      |

## Examples

Strip 15 acacia logs:

``` yaml
acacia_stripping:
  type: "blockfertilizing"
  amount: 15                            # amount of blocks to be changed
  from: "ACACIA_LOG"                    # name of from block
  block: "STRIPPED_ACACIA_LOG"          # name of to block
```
