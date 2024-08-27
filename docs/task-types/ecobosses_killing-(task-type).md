---
title: ecobosses_killing
parent: External task types
grand_parent: Task types
---

# ecobosses_killing (task type)

Since v3.14
{: .label .label-green }

Plugin 'EcoBosses' required
{: .label }

Kill a certain number of EcoBosses bosses.

## Options

| Key             | Description                                      | Type                       | Required | Default | Notes                                         |
|-----------------|--------------------------------------------------|----------------------------|----------|---------|-----------------------------------------------|
| `amount`        | The number of bosses to kill.                    | Integer                    | Yes      | \-      | \-                                            |
| `id` / `ids`    | The EcoBosses boss ID(s).                        | String, or list of strings | Yes      | \-      | \-                                            |
| `id-match-mode` | The match mode to be used to compare the strings | String                     | No       | EQUALS  | One of: `EQUALS`, `STARTS_WITH`, `ENDS_WITH`. |
| `worlds`        | Worlds which should count towards the progress.  | List of world names        | No       | \-      | \-                                            |

## Examples

Kill 1 EcoBosses boss with the ID "skeletalknight":

``` yaml
ecobosses:
  type: "ecobosses_killing"
  amount: 1                             # amount of mobs to be killed
  id: "skeletalknight"                  # internal name of mob (name in config - NOT display name)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
