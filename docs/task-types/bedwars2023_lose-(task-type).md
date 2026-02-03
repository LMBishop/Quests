---
title: bedwars2023_lose
parent: External task types
grand_parent: Task types
---

# bedwars2023_lose (task type)

Not released yet (dev builds)
{: .label .label-green }

Plugin 'BedWars2023' required
{: .label }

Lose a game of BedWars in BedWars2023.

## Options

| Key      | Description                  | Type                | Required | Default | Notes |
|----------|------------------------------|---------------------|----------|---------|-------|
| `amount` | The amount of games to lose. | Integer             | Yes      | \-      | \-    |

## Examples

Lose 5 Game:

``` yaml
bedwars2023:
  type: "bedwars2023_lose"
  amount: 5                           # amount of games to lose
```
