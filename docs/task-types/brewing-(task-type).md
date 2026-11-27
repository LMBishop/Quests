---
title: brewing
parent: Built-in task types
grand_parent: Task types
---

# brewing (task type)

Since v2.0.13
{: .label .label-green }

Minecraft 1.17+ required
{: .label .label-purple }

Brew a set amount of potions, optionally of a specific ingredient.

## Options

| Key           | Description                                                  | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|---------------|--------------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`      | The number of potions to brew.                               | Integer                | Yes      | \-      | Must be at least 1.                                                                                                                                                                                                                                                          |
| `ingredient-requirement` | Item requirement(s) for `ingredient`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `ingredient`. See [item requirements](../configuration/item-requirements). |
| `ingredient`  | The specific ingredient to brew.                             | Material, or ItemStack | No       | Any     | **Deprecated:** will be removed in a future version, use `ingredient-requirement` instead. Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `exact-match` | Whether the ingredient should exactly match what is defined. | Boolean                | No       | true    | **Deprecated:** will be removed in a future version, use `ingredient-requirement` instead. |
| `worlds`      | Worlds which should count towards the progress.              | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Brew 8 potions:

``` yaml
brewing:
  type: "brewing"
  amount: 10                            # amount of potions brewed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Brew 8 potions using sugar:

``` yaml
brewing:
  type: "brewing"
  ingredient: "sugar"
  amount: 10                            # amount of potions brewed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
