---
title: bedwars1058_bedbreak
parent: External task types
grand_parent: Task types
---

# bedwars1058_bedbreak (task type)

Since v3.15
{: .label .label-green }

Plugin 'BedWars1058' required
{: .label }

Break a set amount of beds in BedWars1058.

## Options

| Key      | Description                  | Type                | Required | Default | Notes |
|----------|------------------------------|---------------------|----------|---------|-------|
| `amount` | The amount of beds to break. | Integer             | Yes      | \-      | \-    |

## Examples

Break 10 beds:

``` yaml
bedwars1058:
  type: "bedwars1058_bedbreak"
  amount: 10                            # amount of beds to break
```
