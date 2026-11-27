---
title: mythicmobs_dealdamage
parent: External task types
grand_parent: Task types
---

# mythicmobs_dealdamage (task type)

Since v3.16
{: .label .label-green }

Plugin 'MythicMobs' required
{: .label }

Deal a certain amount of damage to a MythicMobs mob.

## Options

| Key               | Description                                            | Type                | Required | Default | Notes                                                                                                                             |
|-------------------|--------------------------------------------------------|---------------------|----------|---------|-----------------------------------------------------------------------------------------------------------------------------------|
| `amount`          | The amount of damage needed.                           | Integer             | Yes      | \-      | Damage is measured in HP, 1 heart = 2 HP. A player has 20 HP by default, with no status effects applied. Must be at least 1.      |
| `name` / `names`  | The MythicMob ID.                                      | String              | Yes      | \-      | \-                                                                                                                                |
| `name-match-mode` | The match mode to be used to compare the strings       | String              | No       | EQUALS  | One of: `EQUALS`, `STARTS_WITH`, `ENDS_WITH`.                                                                                     |
| `level`           | The level the mob must be at.                          | Integer             | Yes      | \-      | \-                                                                                                                                |
| `min-level`       | The minimum level the mob must be at.                  | Integer             | Yes      | \-      | \-                                                                                                                                |
| `item-requirement` | Item requirement(s) for `item`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `item`. See [item requirements](../configuration/item-requirements). |
| `item`            | Specific item which should be used to deal damage.     | ItemStack           | No       | \-      | **Deprecated:** will be removed in a future version, use `item-requirement` instead. Accepts standard [item definition](../configuration/defining-items). |
| `data`            | The data code for the item.                            | Integer             | No       | 0       | **Deprecated:** will be removed in a future version, use `damage` in `item-requirement` instead. This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions. Must be between 0 and 32767. |
| `exact-match`     | Whether the item should exactly match what is defined. | Boolean             | No       | true    | **Deprecated:** will be removed in a future version, use `item-requirement` instead. |
| `worlds`          | Worlds which should count towards the progress.        | List of world names | No       | \-      | \-                                                                                                                                |
| `ignored-worlds`  | Worlds which should not count towards the progress.    | List of world names | No       | \-      | \-                                                                                                                                |

## Examples

Deal 200 damage to a mythic mob with the ID "SkeletalKnight" of at least level 3:

``` yaml
mythicmobs:
  type: "mythicmobs_dealdamage"
  amount: 200                           # amount of damage to be dealt
  name: "SkeletalKnight"                # internal name of mob (name in config - NOT display name)
  min-level: 3                          # (OPTIONAL) the minimum level the mob must be for it to count
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
