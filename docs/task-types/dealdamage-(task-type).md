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

| Key                    | Description                                     | Type                                 | Required | Default | Notes                                                                                                                                                                                              |
|------------------------|-------------------------------------------------|--------------------------------------|----------|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`               | The amount of damage needed.                    | Integer                              | Yes      | \-      | Damage is measured in HP, 1 heart = 2 HP. A player has 20 HP by default, with no status effects applied.                                                                                           |
| `allow-only-creatures` | Whether the entity must be a creature.          | Boolean                              | No       | True    | \-                                                                                                                                                                                                 |
| `mob` / `mobs`         | The specific mob(s) to kill.                    | Entity type, or list of entity types | No       | \-      | Not specifying this field will allow all mob types to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) for entity types. |
| `worlds`               | Worlds which should count towards the progress. | List of world names                  | No       | \-      | \-                                                                                                                                                                                                 |

## Examples

Deal 100 HP of damage:

``` yaml
dealdamage:
  type: "dealdamage"
  amount: 100                           # amount of damage inflicted (HP)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
