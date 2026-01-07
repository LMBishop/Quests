---
title: walking
parent: Built-in task types
grand_parent: Task types
---

# walking (task type)

Since v2.0
{: .label .label-green }

Walk a set distance.

## Options

| Key        | Description                                     | Type                | Required | Default | Notes                                                                                                                                                                                                                                                                                                                                                                                      |
|------------|-------------------------------------------------|---------------------|----------|---------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `distance` | The distance in metres to walk.                 | Integer             | Yes      | \-      | 1 metre is equivalent to 1 block.                                                                                                                                                                                                                                                                                                                                                          |
| `mode`     | The specific mode to travel                     | String              | No       | \-      | One of: `boat`, `camel`, `donkey`, `happy_ghast`, `horse`, `llama`, `minecart`, `mule`, `nautilus` `pig`, `skeleton_horse`, `strider`, `zombie_horse`, `sneaking`, `walking`, `running`, `swimming`, `flying`, `elytra`. Alternatively one of groups: `ground`, `manual_no_flight`, `manual_no_swim`, `manual` or `vehicle`. Not specifying a mode will allow any of these modes to count. |
| `worlds`   | Worlds which should count towards the progress. | List of world names | No       | \-      | \-                                                                                                                                                                                                                                                                                                                                                                                         |

## Examples

Travel 1000 metres:

``` yaml
walking:
  type: "walking"
  distance: 1000                        # distance in blocks travelled
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Travel 1000 metres by sprinting only:

``` yaml
walking:
  type: "walking"
  distance: 1000                        # distance in blocks travelled
  mode: running                         # (OPTIONAL) specific mode of transport
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
