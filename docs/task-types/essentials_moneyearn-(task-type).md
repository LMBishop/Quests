---
title: essentials_moneyearn
parent: External task types
grand_parent: Task types
---

# essentials_moneyearn (task type)

Since v2.12
{: .label .label-green }

Plugin 'Essentials' required
{: .label }

Earn a certain amount of money after starting quest.

## Options

| Key      | Description         | Type    | Required | Default | Notes |
|----------|---------------------|---------|----------|---------|-------|
| `amount` | The amount to earn. | Integer | Yes      | \-      | \-    |

## Examples

Earn \$1000:

``` yaml
essentialsbalance:
  type: "essentials_moneyearn"
  amount: 1000                          # amount of money to earn
```
