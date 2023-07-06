---
title: playtime
parent: Built-in task types
grand_parent: Task types
---

# playtime (task type)

Since v1.8
{: .label .label-green }

Play for a certain amount of time after starting the quest.

{: .note }
Before Quests v2.0, this task was known as `TIMEPLAYED`.

## Options

| Key          | Description                            | Type    | Required | Default | Notes                |
|--------------|----------------------------------------|---------|----------|---------|----------------------|
| `minutes`    | The number of minutes to play.         | Integer | Yes      | \-      | \-                   |
| `ignore-afk` | Whether AFK players should be ignored. | Boolean | Yes      | \-      | Requires Essentials. |

## Examples

Play for 20 minutes:

``` yaml
playtime:
  type: "playtime"
  minutes: 10                           # amount of minutes played
  ignore-afk: false                     # (OPTIONAL) ignore players marked as AFK by essentials
```
