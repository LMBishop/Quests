---
title: bedwars1058_finalkill
parent: External task types
grand_parent: Task types
---

# bedwars1058_finalkill (task type)

Not released yet (dev builds)
{: .label .label-green }

Plugin 'BedWars1058' required
{: .label }

Get a final kill in a BedWars1058 game.

## Options

| Key      | Description                | Type                | Required | Default | Notes |
|----------|----------------------------|---------------------|----------|---------|-------|
| `amount` | The amount of final kills. | Integer             | Yes      | \-      | \-    |

## Examples

Final kill 5 players:

``` yaml
bedwars1058:
  type: "bedwars1058_finalkill"
  amount: 5                           # amount of final kills
```
