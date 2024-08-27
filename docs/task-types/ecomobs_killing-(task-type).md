---
title: ecomobs_killing
parent: External task types
grand_parent: Task types
---

# ecomobs_killing (task type)

Since v3.15
{: .label .label-green }

Plugin 'EcoMobs' required
{: .label }

Kill a certain number of EcoMobs mob.

## Options

| Key             | Description                                      | Type                       | Required | Default | Notes                                         |
|-----------------|--------------------------------------------------|----------------------------|----------|---------|-----------------------------------------------|
| `amount`        | The number of bosses to kill.                    | Integer                    | Yes      | \-      | \-                                            |
| `id` / `ids`    | The EcoMobs boss ID(s).                          | String, or list of strings | Yes      | \-      | \-                                            |
| `id-match-mode` | The match mode to be used to compare the strings | String                     | No       | EQUALS  | One of: `EQUALS`, `STARTS_WITH`, `ENDS_WITH`. |
| `worlds`        | Worlds which should count towards the progress.  | List of world names        | No       | \-      | \-                                            |

## Examples

Kill 1 EcoMobs mobs with the ID "skeletalknight":

``` yaml
ecobosses:
  type: "ecomobs_killing"
  amount: 1                             # amount of mobs to be killed
  id: "skeletalknight"                  # internal name of mob (name in config - NOT display name)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
