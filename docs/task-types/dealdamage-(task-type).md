---
title: dealdamage
parent: Built-in task types
grand_parent: Task types
---

# dealdamage (task type)

Since v2.2
{: .label .label-green }

Deal a certain amount of damage.

## Options

| Key                    | Description                                            | Type                                 | Required | Default | Notes                                                                                                                                                                                              |
|------------------------|--------------------------------------------------------|--------------------------------------|----------|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`               | The amount of damage needed.                           | Integer                              | Yes      | \-      | Damage is measured in HP, 1 heart = 2 HP. A player has 20 HP by default, with no status effects applied.                                                                                           |
| `allow-only-creatures` | Whether the entity must be a creature.                 | Boolean                              | No       | True    | \-                                                                                                                                                                                                 |
| `mob` / `mobs`         | The specific mob(s) to deal damage to.                 | Entity type, or list of entity types | No       | \-      | Not specifying this field will allow all mob types to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) for entity types. |
| `item`                 | Specific item which should be used to deal damage.     | ItemStack                            | No       | \-      | Accepts standard [item definition](../configuration/defining-items).                                                                                                                               |
| `data`                 | The data code for the item.                            | Integer                              | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions.                                                                                               |
| `exact-match`          | Whether the item should exactly match what is defined. | Boolean                              | No       | true    | \-                                                                                                                                                                                                 |
| `worlds`               | Worlds which should count towards the progress.        | List of world names                  | No       | \-      | \-                                                                                                                                                                                                 |

## Examples

Deal 100 HP of damage:

``` yaml
dealdamage:
  type: "dealdamage"
  amount: 100                           # amount of damage inflicted (HP)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
