Breed a certain amount of animals.

## Options

| Key      | Description                                     | Type                | Required | Default | Notes |
|----------|-------------------------------------------------|---------------------|----------|---------|-------|
| `amount` | The number of animals to breed.                 | Integer             | Yes      | \-      | \-    |
| `worlds` | Worlds which should count towards the progress. | List of world names | No       | \-      | \-    |

## Examples

Breed 5 animals:

``` yaml
breeding:
  type: "breeding"
  amount: 5                             # amount of animals bred
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
