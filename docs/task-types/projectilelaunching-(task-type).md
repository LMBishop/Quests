---
title: projectilelaunching
parent: Built-in task types
grand_parent: Task types
---

# projectilelaunching (task type)

Since v3.15
{: .label .label-green }

Launch a certain number of projectiles. This happens when 
a player fires a bow, throws a snowball, etc.

## Options

| Key                          | Description                                     | Type                             | Required | Default | Notes                                                                                                                                                                                                 |
|------------------------------|-------------------------------------------------|----------------------------------|----------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`                     | The number of projectiles to launch.            | Integer                          | Yes      | \-      | \-                                                                                                                                                                                                    |
| `projectile` / `projectiles` | The specific projectile(s) to launch.           | Entity type, or list of entities | No       | \-      | Not specifying this field will allow all entity types to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) for entity types. |
| `worlds`                     | Worlds which should count towards the progress. | List of world names              | No       | \-      | \-                                                                                                                                                                                                    |

## Examples

Shoot 5 arrows:

``` yaml
projectilelaunching:
  type: "projectilelaunching"
  amount: 5                             # amount of projectiles launched
  projectile: 'ARROW'                   # the projectile to launch
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Throw 5 snowballs:

``` yaml
projectilelaunching:
  type: "projectilelaunching"
  amount: 5                             # amount of projectiles launched
  projectile: 'SNOWBALL'                # the projectile to launch
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
