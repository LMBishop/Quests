---
title: shopguiplus_sell
parent: External task types
grand_parent: Task types
---

# shopguiplus_sell (task type)

Since v2.15
{: .label .label-green }

Plugin 'ShopGUI+' required
{: .label }

Sell a certain number of items to a ShopGUI+ shop.

{: .note }
Since Quests v3.13, `shopguiplus_sellcertain` and `shopguiplus_sell`
have been merged into one. Both names can be used to refer to this task.

## Options

| Key       | Description                                     | Type                | Required | Default | Notes |
|-----------|-------------------------------------------------|---------------------|----------|---------|-------|
| `amount`  | The number of items to sell.                    | Integer             | Yes      | \-      | \-    |
| `shop-id` | The ID of the shop.                             | String              | Yes      | \-      | \-    |
| `item-id` | The ID of the item to sell.                     | String              | Yes      | \-      | \-    |
| `worlds`  | Worlds which should count towards the progress. | List of world names | No       | \-      | \-    |

## Examples

Sell 1000 of item_id to shop_id:

``` yaml
shopguiplussell:
  type: "shopguiplus_sell"
  shop-id: "shop_id"                    # shopgui+ id of the shop that contains the item.
  item-id: "item_id"                    # shopgui+ id of item to sell
  amount: 1000                          # amount of thing to sell
```
