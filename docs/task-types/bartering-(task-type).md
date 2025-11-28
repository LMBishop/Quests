---
title: bartering
parent: Built-in task types
grand_parent: Task types
---

# bartering (task type)

Since v3.16
{: .label .label-green }

Minecraft 1.16.5+ required
{: .label .label-purple }

Make a bartering interaction with a piglin.

## Options

| Key                  | Description                                                   | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|----------------------|---------------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`             | The number of items.                                          | Integer                | Yes      | \-      | \-                                                                                                                                                                                                                                                                           |
| `input`              | The specific item to be picked up by a piglin.                | Material, or ItemStack | No       | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `input-exact-match`  | Whether the input item should exactly match what is defined.  | Boolean                | No       | true    | \-                                                                                                                                                                                                                                                                           |
| `output`             | The specific item to be dropped by a piglin.                  | Material, or ItemStack | No       | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `output-exact-match` | Whether the output item should exactly match what is defined. | Boolean                | No       | true    | \-                                                                                                                                                                                                                                                                           |
| `mode`               | The specific mode of bartering.                               | String                 | No       | output  | One of: `input`, `output`.                                                                                                                                                                                                                                                   |
| `worlds`             | Worlds which should count towards the progress.               | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Get 100 spectral arrows for gold ingots from a piglin:

``` yaml
barter_spectral_arrows:
  type: "bartering"
  amount: 100                           # amount of items to be dropped (with output mode) by a piglin
  mode: output                          # (OPTIONAL) whether output or input items should count towards the progress
  input: GOLD_INGOT                     # (OPTIONAL) the item to be picked up by a piglin
  output: SPECTRAL_ARROW                # (OPTIONAL) the item to be dropped by a piglin
  input-exact-match: false              # (OPTIONAL) we don't care about item nbt
  output-exact-match: false             # (OPTIONAL) we don't care about item nbt
  worlds:                               # (OPTIONAL) restrict to certain worlds
    - "world_nether"
```
