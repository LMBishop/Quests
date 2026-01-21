---
title: bedwars2023_win
parent: External task types
grand_parent: Task types
---

# bedwars2023_win (task type)

Not released yet (dev builds)
{: .label .label-green }

Plugin 'BedWars2023' required
{: .label }

Win a game of BedWars in BedWars2023.

## Options

| Key      | Description                 | Type                | Required | Default | Notes |
|----------|-----------------------------|---------------------|----------|---------|-------|
| `amount` | The amount of games to win. | Integer             | Yes      | \-      | \-    |

## Examples

Win 5 games:

``` yaml
bedwars2023:
  type: "bedwars2023_win"
  amount: 5                           # amount of games to win
```
