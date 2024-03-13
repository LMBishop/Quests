---
title: mobkilling
parent: Built-in task types
grand_parent: Task types
---

# mobkilling (task type)

Since v2.0
{: .label .label-green }

Kill a set amount of mobs.

This task type has specific logic implemented for compatibility with
[WildStacker](https://bg-software.com/wildstacker/).

{: .note }
Since Quests v3.13, `mobkillingcertain` and `mobkilling` have been
merged into one. Both names can be used to refer to this task.

## Options

| Key            | Description                                            | Type                                   | Required | Default | Notes                                                                                                                                                                                                                                                                          |
|----------------|--------------------------------------------------------|----------------------------------------|----------|---------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`       | The number of mobs to kill.                            | Integer                                | Yes      | \-      | \-                                                                                                                                                                                                                                                                             |
| `mob`          | The specific mob(s) to kill.                           | Entity type, or list of entity types   | No       | \-      | Not specifying this field will allow all mob types to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) for entity types.                                                                             |
| `name`         | The specific name(s) of mobs to kill.                  | String, or list of strings             | No       | \-      | Not specifying this field will allow mobs with any names to count towards the task. Ensure any colour codes in the name of the entity are specified, otherwise the match will not work.                                                                                        |
| `spawn-reason` | The specific spawn reason(s) of mobs to kill.          | Spawn reason, or list of spawn reasons | No       | \-      | Works only on Paper and its forks. Not specifying this field will allow mobs with any spawn reasons to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html) for spawn reasons. |
| `hostile`      | Whether the mob must be hostile.                       | Boolean                                | No       | \-      | If specified and set to false, only non-hostile mobs will count.                                                                                                                                                                                                               |
| `item`         | Specific item which should be used to kill mobs.       | ItemStack                              | No       | \-      | Accepts standard [item definition](../configuration/defining-items).                                                                                                                                                                                                           |
| `worlds`       | Worlds which should count towards the progress.        | List of world names                    | No       | \-      | \-                                                                                                                                                                                                                                                                             |
| `exact-match`  | Whether the item should exactly match what is defined. | Boolean                                | No       | true    | \-                                                                                                                                                                                                                                                                             |

## Examples

Kill 10 of any mob:

``` yaml
mobkilling:
  type: "mobkilling"
  amount: 10                            # amount of mobs to be killed
  hostile: true                         # (OPTIONAL) whether or not the mob is hostile - default: both
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Kill 10 of a blaze named (red) "Inferno":

``` yaml
mobkillingblaze:
  type: "mobkilling"
  amount: 10                            # amount of mobs to be killed
  mob: BLAZE                            # (OPTIONAL) type of mob
  name: &cInferno                       # (OPTIONAL) this only allows blazes called "&cInferno" - default: any name
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Kill 10 of a blade named either (red) "Inferno" or (orange) "Furnace":

``` yaml
mobkillingblazemultiple:
  type: "mobkilling"
  amount: 10                            # amount of mobs to be killed
  mob: BLAZE                            # (OPTIONAL) type of mob
  names:                                # (OPTIONAL) this only allows blazes called "&cInferno" OR "&6Furnace" - default: any name
   - "&cInferno"
   - "&6Furnace"
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Kill 10 of a blaze or creeper named either (red) "Inferno" or (orange)
"Furnace":

``` yaml
mobkillingblazemultiple:
  type: "mobkilling"
  amount: 10                            # amount of mobs to be killed
  mob:                                  # (OPTIONAL) types of mobs
   - BLAZE                              
   - CREEPER
  names:                                # (OPTIONAL) this only allows blazes called "&cInferno" OR "&6Furnace" - default: any name
   - "&cInferno"
   - "&6Furnace"
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Kill 10 of a blaze or creeper named either (red) "Inferno" or (orange)
"Furnace" with [quest item](../configuration/defining-items#quest-items)
"super_sword":

``` yaml
mobkillingblazecreepermultiple:
  type: "mobkilling"
  amount: 10                            # amount of mobs to be killed
  mob:                                  # (OPTIONAL) types of mobs
   - BLAZE                              
   - CREEPER
  names:                                # (OPTIONAL) this only allows blazes called "&cInferno" OR "&6Furnace" - default: any name
   - "&cInferno"
   - "&6Furnace"
  item:                                 # (OPTIONAL) specific item to kill with
    quest-item: "super_sword"
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
