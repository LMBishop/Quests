# bedwars1058_buy (task type)

Plugin 'BedWars1058' required  
{: .label }

Buy a specific item from the BedWars1058 shop.

## Options

| Key      | Description                                   | Type    | Required | Default | Notes                     |
|----------|-----------------------------------------------|---------|----------|---------|---------------------------|
| `item`   | The shop item that the player must purchase. | String  | Yes      | \-      | \-      |
| `amount` | The number of times to buy this item.         | Integer | Yes      | \-      | \-      |

## Examples

Buy 3 Ender Pearls:

```yaml
buy_enderpearl:
  type: "bedwars1058_buy"
  item: "ENDER_PEARL"
  amount: 3
```