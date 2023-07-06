---
title: playerpoints_earn
parent: External task types
grand_parent: Task types
---

# playerpoints_earn (task type)

Since v3.14
{: .label .label-green }

Plugin 'PlayerPoints' required
{: .label }

Earn a number of points.

## Options

| Key      | Description           | Type    | Required | Default | Notes |
|----------|-----------------------|---------|----------|---------|-------|
| `amount` | The number of points. | Integer | Yes      | \-      | \-    |

## Examples

Earn 10 points:

``` yaml
playerpoints:
  type: "playerpoints_earn"
  amount: 10                             # number of points to earn
```
