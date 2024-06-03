---
title: replenishing
parent: Built-in task types
grand_parent: Task types
---

# replenishing (task type)

Since v3.14
{: .label .label-green }


Replenish a set amount of certain blocks or entities.

## Options

| Key                | Description                                     | Type                             | Required | Default | Notes                                                                                                                                                                                                                                                                              |
|--------------------|-------------------------------------------------|----------------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`           | The number of blocks or entities to replenish.  | Integer                          | Yes      | \-      | \-                                                                                                                                                                                                                                                                                 |
| `block` / `blocks` | The specific block(s) to replenish.             | Material, or list of material    | No       | \-      | Not specifying this field will allow all blocks to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `mob` / `mobs`     | The specific mob(s) to replenish.               | Entity type, or list of entities | No       | \-      | Not specifying this field will allow all mob types to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) for entity types.                                                                                 |
| `worlds`           | Worlds which should count towards the progress. | List of world names              | No       | \-      | \-                                                                                                                                                                                                                                                                                 |

## Examples

Replenish 10 inventories:

``` yaml
replenishing:
  type: "replenishing"
  amount: 10                            # amount of inventories replenished
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
