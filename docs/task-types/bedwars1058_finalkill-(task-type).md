---
title: bedwars1058_finalkill
parent: External task types
grand_parent: Task types
---

# bedwars1058_finalkill (task type)

Since v3.15
{: .label .label-green }

Plugin 'BedWars1058' required
{: .label }

Get a final kill in BedWars1058.

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
