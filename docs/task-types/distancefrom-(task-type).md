---
title: distancefrom
parent: Built-in task types
grand_parent: Task types
---

# distancefrom (task type)

Since v2.12
{: .label .label-green }

Travel away from a set of co-ordinates.

## Options

| Key        | Description                                                     | Type    | Required | Default | Notes |
|------------|-----------------------------------------------------------------|---------|----------|---------|-------|
| `x`        | The x co-ordinate.                                              | Integer | Yes      | \-      | \-    |
| `y`        | The y co-ordinate.                                              | Integer | Yes      | \-      | \-    |
| `z`        | The z co-ordinate.                                              | Integer | Yes      | \-      | \-    |
| `world`    | The name of the world.                                          | String  | Yes      | \-      | \-    |
| `distance` | The distance away from the co-ordinates the player must travel. | Integer | Yes      | \-      | \-    |

## Examples

Travel at least 10 blocks away from (0, 0, 0) in world:

``` yaml
distancefrom:
  type: "distancefrom"
  x: 0                                  # x position
  y: 0                                  # y position
  z: 0                                  # z position
  world: world                          # name of world
  distance: 10                          # required distance from coordinates
```
