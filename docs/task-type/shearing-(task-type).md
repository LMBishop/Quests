Shear a set amount of sheep.

## Options

| Key      | Description                                     | Type                | Required | Default | Notes |
|----------|-------------------------------------------------|---------------------|----------|---------|-------|
| `amount` | The number of sheep to shear.                   | Integer             | Yes      | \-      | \-    |
| `worlds` | Worlds which should count towards the progress. | List of world names | No       | \-      | \-    |

## Examples

Shear 10 sheep:

``` yaml
shearing:
  type: "shearing"
  amount: 10                            # amount of sheep sheared
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
