---
title: mythicmobs_killing
parent: External task types
grand_parent: Task types
---

# mythicmobs_killing (task type)

Since v2.3
{: .label .label-green }

Plugin 'MythicMobs' required
{: .label }

Kill a certain MythicMobs mob.

## Options

| Key               | Description                                      | Type                | Required | Default | Notes                                         |
|-------------------|--------------------------------------------------|---------------------|----------|---------|-----------------------------------------------|
| `amount`          | The number of mobs to kill.                      | Integer             | Yes      | \-      | \-                                            |
| `name` / `names`  | The MythicMob ID.                                | String              | Yes      | \-      | \-                                            |
| `name-match-mode` | The match mode to be used to compare the strings | String              | No       | EQUALS  | One of: `EQUALS`, `STARTS_WITH`, `ENDS_WITH`. |
| `level`           | The level the mob must be at.                    | Integer             | Yes      | \-      | \-                                            |
| `min-level`       | The minimum level the mob must be at.            | Integer             | Yes      | \-      | \-                                            |
| `worlds`          | Worlds which should count towards the progress.  | List of world names | No       | \-      | \-                                            |

## Examples

Kill 1 mythic mob with the ID "SkeletalKnight":

``` yaml
mythicmobs:
  type: "mythicmobs_killing"
  amount: 1                             # amount of mobs to be killed
  name: "SkeletalKnight"                # internal name of mob (name in config - NOT display name)
  level: 1                              # (OPTIONAL) the exact level the mob must be for it to count
  min-level: 1                          # (OPTIONAL) the minimum level the mob must be for it to count
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
