---
title: trading
parent: Built-in task types
grand_parent: Task types
---

# trading (task type)

Not released yet (dev builds)
{: .label .label-green }

Minecraft 1.16+ required
{: .label .label-purple }

Paper required
{: .label .label-yellow }

Trade with a Villager or Wandering Trader.

## Options

| Key           | Description                                            | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|---------------|--------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`      | The number of items to trade.                          | Integer                | Yes      | \-      | \-                                                                                                                                                                                                                                                                           |
| `item`        | The specific item to trade.                            | Material, or ItemStack | No       | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `exact-match` | Whether the item should exactly match what is defined. | Boolean                | No       | true    | \-                                                                                                                                                                                                                                                                           |
| `worlds`      | Worlds which should count towards the progress.        | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Buy 10 carrots from a Villager:

``` yaml
tradecarrots:
  type: "trading"
  amount: 10                            # amount of items to trade
  item: CARROT                          # (OPTIONAL) item to trade
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
