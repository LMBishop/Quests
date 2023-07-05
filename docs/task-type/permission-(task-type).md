Test if a player has a permission.

## Options

| Key          | Description                            | Type   | Required | Default | Notes |
|--------------|----------------------------------------|--------|----------|---------|-------|
| `permission` | The permission the player should have. | String | Yes      | \-      | \-    |

## Examples

Check if player has permission `some.permission.name`:

``` yaml
permission:
  type: "permission"
  permission: "some.permission.name"    # permission required to be marked as complete
```
