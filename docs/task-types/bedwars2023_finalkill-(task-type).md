---
title: bedwars2023_finalkill
parent: External task types
grand_parent: Task types
---

# bedwars2023_finalkill (task type)

Not released yet (dev builds)
{: .label .label-green }

Plugin 'BedWars2023' required
{: .label }

Get a final kill in a BedWars2023 game.

## Options

| Key      | Description                | Type                | Required | Default | Notes |
|----------|----------------------------|---------------------|----------|---------|-------|
| `amount` | The amount of final kills. | Integer             | Yes      | \-      | \-    |

## Examples

Final kill 5 players:

``` yaml
bedwars2023:
  type: "bedwars2023_finalkill"
  amount: 5                           # amount of final kills
```
