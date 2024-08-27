---
title: shopguiplus_buy
parent: External task types
grand_parent: Task types
---

# shopguiplus_buy (task type)

Since v2.15
{: .label .label-green }

Plugin 'ShopGUI+' required
{: .label }

Buy a certain number of items from a ShopGUI+ shop.

{: .note }
Since Quests v3.13, `shopguiplus_buycertain` and `shopguiplus_buy` have
been merged into one. Both names can be used to refer to this task.

## Options

| Key       | Description                                     | Type                | Required | Default | Notes |
|-----------|-------------------------------------------------|---------------------|----------|---------|-------|
| `amount`  | The number of items to buy.                     | Integer             | Yes      | \-      | \-    |
| `shop-id` | The ID of the shop.                             | String              | Yes      | \-      | \-    |
| `item-id` | The ID of the item to buy.                      | String              | Yes      | \-      | \-    |
| `worlds`  | Worlds which should count towards the progress. | List of world names | No       | \-      | \-    |

## Examples

Buy 1000 of item_id from shop_id:

``` yaml
shopguiplusbuy:
  type: "shopguiplus_buy"
  shop-id: "shop_id"                    # shopgui+ id of the shop that contains the item.
  item-id: "item_id"                    # shopgui+ id of item to sell
  amount: 1000                          # amount of thing to sell
```
