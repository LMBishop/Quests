Brew a set amount of potions.

## Options

| Key      | Description                                     | Type                | Required | Default | Notes |
|----------|-------------------------------------------------|---------------------|----------|---------|-------|
| `amount` | The number of potions to brew.                  | Integer             | Yes      | \-      | \-    |
| `worlds` | Worlds which should count towards the progress. | List of world names | No       | \-      | \-    |

## Examples

Brew 8 potions:

``` yaml
brewing:
  type: "brewing"
  amount: 10                            # amount of potions brewed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
