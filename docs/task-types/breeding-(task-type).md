---
title: breeding
parent: Built-in task types
grand_parent: Task types
---

# breeding (task type)

Since v2.2 
{: .label .label-green }

Breed a certain amount of animals.

This task type has specific logic implemented for compatibility with
[WildStacker](https://bg-software.com/wildstacker/).

## Options

| Key            | Description                                     | Type                             | Required | Default | Notes                                                                                                                                                                                              |
|----------------|-------------------------------------------------|----------------------------------|----------|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`       | The number of animals to breed.                 | Integer                          | Yes      | \-      | \-                                                                                                                                                                                                 |
| `mob` / `mobs` | The specific mob(s) to breed.                   | Entity type, or list of entities | No       | \-      | Not specifying this field will allow all mob types to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) for entity types. |
| `worlds`       | Worlds which should count towards the progress. | List of world names              | No       | \-      | \-                                                                                                                                                                                                 |

## Examples

Breed 5 animals:

``` yaml
breeding:
  type: "breeding"
  amount: 5                             # amount of animals bred
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Breed 5 pigs:

``` yaml
breeding:
  type: "breeding"
  amount: 5                             # amount of animals bred
  mob: "PIG"                            # type of mob
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
