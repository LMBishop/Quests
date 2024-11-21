---
title: bedwars1058_lose
parent: External task types
grand_parent: Task types
---

# bedwars1058_lose (task type)

Since v3.15
{: .label .label-green }

Plugin 'BedWars1058' required
{: .label }

Lose a game of BedWars in BedWars1058.

## Options

| Key      | Description                  | Type                | Required | Default | Notes |
|----------|------------------------------|---------------------|----------|---------|-------|
| `amount` | The amount of games to lose. | Integer             | Yes      | \-      | \-    |

## Examples

Lose 5 Game:

``` yaml
bedwars1058:
  type: "bedwars1058_lose"
  amount: 5                           # amount of games to lose
```
