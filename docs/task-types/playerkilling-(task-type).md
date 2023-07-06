---
title: playerkilling
parent: Built-in task types
grand_parent: Task types
---

# playerkilling (task type)

Since v2.0
{: .label .label-green }


Kill a set amount of players.

## Options

| Key      | Description                                     | Type                | Required | Default | Notes |
|----------|-------------------------------------------------|---------------------|----------|---------|-------|
| `amount` | The number of players to kill.                  | Integer             | Yes      | \-      | \-    |
| `worlds` | Worlds which should count towards the progress. | List of world names | No       | \-      | \-    |

## Examples

Kill 10 of any player:

``` yaml
killplayers:
  type: "playerkilling"
  amount: 10                            # amount of players to kill
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
