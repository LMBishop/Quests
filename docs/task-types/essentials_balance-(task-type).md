---
title: essentials_balance
parent: External task types
grand_parent: Task types
---

# essentials_balance (task type)

Since v2.12
{: .label .label-green }

Plugin 'Essentials' required
{: .label }

Reach a certain balance.

## Options

| Key      | Description          | Type    | Required | Default | Notes |
|----------|----------------------|---------|----------|---------|-------|
| `amount` | The amount to reach. | Integer | Yes      | \-      | \-    |

## Examples

Reach a balance of \$1000:

``` yaml
essentialsbalance:
  type: "essentials_balance"
  amount: 1000                          # amount of money to rach
```
