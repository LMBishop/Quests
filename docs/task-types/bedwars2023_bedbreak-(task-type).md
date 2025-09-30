---
title: bedwars2023_bedbreak
parent: External task types
grand_parent: Task types
---

# bedwars2023_bedbreak (task type)

Not released yet (dev builds)
{: .label .label-green }

Plugin 'BedWars2023' required
{: .label }

Break a set amount of beds in BedWars2023.

## Options

| Key      | Description                  | Type                | Required | Default | Notes |
|----------|------------------------------|---------------------|----------|---------|-------|
| `amount` | The amount of beds to break. | Integer             | Yes      | \-      | \-    |

## Examples

Break 10 beds:

``` yaml
bedwars2023:
  type: "bedwars2023_bedbreak"
  amount: 10                            # amount of beds to break
```
