---
title: shearing
parent: Built-in task types
grand_parent: Task types
---

# shearing (task type)

Since v2.0
{: .label .label-green }


Shear a set amount of colorables (colourables) or shearables.

## Options

| Key                | Description                                     | Type                             | Required | Default | Notes                                                                                                                                                                                              |
|--------------------|-------------------------------------------------|----------------------------------|----------|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`           | The number of sheep to shear.                   | Integer                          | Yes      | \-      | \-                                                                                                                                                                                                 |
| `color` / `colors` | The specific color(s) to shear.                 | Color / list of colors           | No       | \-      | Not specifying this field will allow all colors to count towards this task. For a list of valid colors, visit [this page](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html).      |
| `mob` / `mobs`     | The specific mob(s) to shear.                   | Entity type, or list of entities | No       | \-      | Not specifying this field will allow all mob types to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) for entity types. |
| `worlds`           | Worlds which should count towards the progress. | List of world names              | No       | \-      | \-                                                                                                                                                                                                 |

## Examples

Shear 10 animals:

``` yaml
shearing:
  type: "shearing"
  amount: 10                            # amount of sheep sheared
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Shear 10 pink sheep:

``` yaml
shearing:
  type: "shearing"
  amount: 10                            # amount of sheep sheared
  color: "PINK"
  mob: "SHEEP"
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
