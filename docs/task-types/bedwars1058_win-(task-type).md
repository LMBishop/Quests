---
title: bedwars1058_win
parent: External task types
grand_parent: Task types
---

# bedwars1058_win (task type)

Since v3.15
{: .label .label-green }

Plugin 'BedWars1058' required
{: .label }

Win a game of BedWars in BedWars1058.

## Options

| Key      | Description                 | Type                | Required | Default | Notes |
|----------|-----------------------------|---------------------|----------|---------|-------|
| `amount` | The amount of games to win. | Integer             | Yes      | \-      | \-    |

## Examples

Win 5 Game:

``` yaml
bedwars1058:
  type: "bedwars1058_win"
  amount: 5                           # amount of games to win
```
