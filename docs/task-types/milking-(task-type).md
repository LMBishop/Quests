---
title: milking
parent: Built-in task types
grand_parent: Task types
---

# milking (task type)

Since v2.0
{: .label .label-green }

Milk a set amount of cows.

## Options

| Key                              | Description                                     | Type                                   | Required | Default | Notes                                                                                                                                                                                                                                                                          |
|----------------------------------|-------------------------------------------------|----------------------------------------|----------|---------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`                         | The number of mobs to milk.                     | Integer                                | Yes      | \-      | \-                                                                                                                                                                                                                                                                             |
| `mob` / `mobs`                   | The specific mob(s) to milk.                    | Entity type, or list of entity types   | No       | \-      | Not specifying this field will allow all mob types to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) for entity types.                                                                             |
| `spawn-reason` / `spawn-reasons` | The specific spawn reason(s) of mobs to milk.   | Spawn reason, or list of spawn reasons | No       | \-      | Works only on Paper and its forks. Not specifying this field will allow mobs with any spawn reasons to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html) for spawn reasons. |
| `worlds`                         | Worlds which should count towards the progress. | List of world names                    | No       | \-      | \-                                                                                                                                                                                                                                                                             |

## Examples

Milk 10 cows:

``` yaml
milking:
  type: "milking"
  amount: 10                            # amount of cows milked
  mob: COW                              # (OPTIONAL) restrict to certain mobs
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
