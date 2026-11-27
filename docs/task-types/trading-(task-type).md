---
title: trading
parent: Built-in task types
grand_parent: Task types
---

# trading (task type)

Since v3.15.1
{: .label .label-green }

Minecraft 1.16+ required
{: .label .label-purple }

Paper required
{: .label .label-yellow }

Trade with a Villager or Wandering Trader.

## Options

| Key                             | Description                                                              | Type                             | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|---------------------------------|--------------------------------------------------------------------------|----------------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`                        | The number of items to trade.                                            | Integer                          | Yes      | \-      | Must be at least 1.                                                                                                                                                                                                                                                          |
| `mob` / `mobs`                  | The specific mob(s) to trade with.                                       | Entity type, or list of entities | No       | \-      | Not specifying this field will allow all mob types to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) for entity types.                                                                           |
| `item-requirement` | Item requirement(s) for `item`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `item`. See [item requirements](../configuration/item-requirements). |
| `item`                          | The specific item to trade.                                              | Material, or ItemStack           | No       | \-      | **Deprecated:** will be removed in a future version, use `item-requirement` instead. Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `exact-match`                   | Whether the item should exactly match what is defined.                   | Boolean                          | No       | true    | **Deprecated:** will be removed in a future version, use `item-requirement` instead. |
| `first-ingredient-requirement` | Item requirement(s) for `first-ingredient`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `first-ingredient`. See [item requirements](../configuration/item-requirements). |
| `first-ingredient`              | The specific item to be used as the first ingredient in a trade.         | Material, or ItemStack           | No       | \-      | **Deprecated:** will be removed in a future version, use `first-ingredient-requirement` instead. Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `first-ingredient-exact-match`  | Whether the first ingredient item should exactly match what is defined.  | Boolean                          | No       | true    | **Deprecated:** will be removed in a future version, use `first-ingredient-requirement` instead. |
| `second-ingredient-requirement` | Item requirement(s) for `second-ingredient`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `second-ingredient`. See [item requirements](../configuration/item-requirements). |
| `second-ingredient`             | The specific item to be used as the second ingredient in a trade.        | Material, or ItemStack           | No       | \-      | **Deprecated:** will be removed in a future version, use `second-ingredient-requirement` instead. Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `second-ingredient-exact-match` | Whether the second ingredient item should exactly match what is defined. | Boolean                          | No       | true    | **Deprecated:** will be removed in a future version, use `second-ingredient-requirement` instead. |
| `mode`                          | The specific mode of trading.                                            | String                           | No       | result  | One of: `result`, `first_ingredient`, `second_ingredient`, `count`.                                                                                                                                                                                                          |
| `worlds`                        | Worlds which should count towards the progress.                          | List of world names              | No       | \-      | \-                                                                                                                                                                                                                                                                           |
| `ignored-worlds`                | Worlds which should not count towards the progress.                      | List of world names              | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Buy 10 carrots from a Villager:

``` yaml
tradecarrots:
  type: "trading"
  amount: 10                            # amount of items to trade
  mob: VILLAGER                         # (OPTIONAL) trader type
  item: CARROT                          # (OPTIONAL) item to trade
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
