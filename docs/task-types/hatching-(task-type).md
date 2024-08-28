---
title: hatching
parent: Built-in task types
grand_parent: Task types
---

# hatching (task type)

Since v3.15.1
{: .label .label-green }

Minecraft 1.15+ required
{: .label .label-purple }

Paper required
{: .label .label-yellow }

Hatch a set amount of entities of certain types.

## Options

| Key            | Description                                     | Type                             | Required | Default | Notes                                                                                                                                                                                                 |
|----------------|-------------------------------------------------|----------------------------------|----------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`       | The number of entities to hatch.                | Integer                          | Yes      | \-      | \-                                                                                                                                                                                                    |
| `mob` / `mobs` | The specific entity type(s) to hatch.           | Entity type, or list of entities | No       | \-      | Not specifying this field will allow all entity types to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) for entity types. |
| `worlds`       | Worlds which should count towards the progress. | List of world names              | No       | \-      | \-                                                                                                                                                                                                    |

## Examples

Hatch a chicken (by throwing an egg):

``` yaml
hatching:
  type: "hatching"
  amount: 1                             # amount of entities to be hatched
  mob: CHICKEN                          # (OPTIONAL) types of mobs
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
