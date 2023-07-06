---
title: permission
parent: Built-in task types
grand_parent: Task types
---

# permission (task type)

Since v2.9.5
{: .label .label-green }

Test if a player has a permission.

This task works by regularly polling a player at a certain interval.

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
