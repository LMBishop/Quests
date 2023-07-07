---
title: taming
parent: Built-in task types
grand_parent: Task types
---

# taming (task type)

Since v3.13
{: .label .label-green }

Tame a set amount of animals.

## Options

| Key            | Description                                     | Type                             | Required | Default | Notes                                                                                                                                                                                              |
|----------------|-------------------------------------------------|----------------------------------|----------|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`       | The number of animals to tame.                  | Integer                          | Yes      | \-      | \-                                                                                                                                                                                                 |
| `mob` / `mobs` | The specific mob(s) to tame.                    | Entity type, or list of entities | No       | \-      | Not specifying this field will allow all mob types to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) for entity types. |
| `worlds`       | Worlds which should count towards the progress. | List of world names              | No       | \-      | \-                                                                                                                                                                                                 |

## Examples

Tame 10 animals:

``` yaml
taming:
  type: "taming"
  amount: 10                            # amount of mobs tamed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Tame 10 pigs:

``` yaml
taming:
  type: "taming"
  amount: 10                            # amount of mobs tamed
  mob: "PIG"                            # mob to tame
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
