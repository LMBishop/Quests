---
title: bedwars2023_buy
parent: External task types
grand_parent: Task types
---

# bedwars2023_buy (task type)

Not released yet (dev builds)
{: .label .label-green }

Plugin 'BedWars2023' required  
{: .label }

Buy a specific item from the BedWars2023 shop.

## Options

| Key      | Description                                   | Type    | Required | Default | Notes                     |
|----------|-----------------------------------------------|---------|----------|---------|---------------------------|
| `item`   | The shop item that the player must purchase. | String  | Yes      | \-      | \-      |
| `amount` | The number of times to buy this item.         | Integer | Yes      | \-      | \-      |

## Examples

Buy 3 Ender Pearls:

``` yaml
bedwars2023:
  type: "bedwars2023_buy"
  item: "ENDER_PEARL"
  amount: 3
```