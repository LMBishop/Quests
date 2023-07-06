---
title: position
parent: Built-in task types
grand_parent: Task types
---

# position (task type)

Since v2.0.3
{: .label .label-green }

Reach a set of co-ordinates.

## Options

| Key                | Description                                               | Type    | Required | Default | Notes                                                                |
|--------------------|-----------------------------------------------------------|---------|----------|---------|----------------------------------------------------------------------|
| `x`                | The x co-ordinate.                                        | Integer | Yes      | \-      | \-                                                                   |
| `y`                | The y co-ordinate.                                        | Integer | Yes      | \-      | \-                                                                   |
| `z`                | The z co-ordinate.                                        | Integer | Yes      | \-      | \-                                                                   |
| `world`            | The name of the world.                                    | String  | Yes      | \-      | \-                                                                   |
| `distance-padding` | The distance around the co-ordinate the player can reach. | Integer | No       | \-      | Having no padding means the player must reach the exact co-ordinate. |

## Examples

Travel to within 10 blocks of (0, 0, 0) in world:

``` yaml
position:
  type: "position"
  x: 0                                  # x position
  y: 0                                  # y position
  z: 0                                  # z position
  world: world                          # name of world
  distance-padding: 10                  # (OPTIONAL) padding zone in meters/blocks - this will allow within 10 blocks of 0, 0, 0 - default = 0
```
