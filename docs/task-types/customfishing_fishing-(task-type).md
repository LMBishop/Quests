---
title: customfishing_fishing
parent: External task types
grand_parent: Task types
---

# customfishing_fishing (task type)

Not released yet (dev builds)
{: .label .label-green }

Plugin 'CustomFishing' required
{: .label }

Catch a set amount of CustomFishing loots.

## Options

| Key      | Description                          | Type    | Required | Default | Notes                                                              |
|----------|--------------------------------------|---------|----------|---------|--------------------------------------------------------------------|
| `amount` | The number of loots to catch.        | Integer | Yes      | \-      | \-                                                                 |
| `loot`   | The specific loot id to catch.       | String  | No       | \-      | If this value is not specified, then any loot will count.          |
| `group`  | The specific group of loot to catch. | String  | No       | \-      | If this value is not specified, then any group of loot will count. |

## Examples

Catch 10 CustomFishing loots:

```yaml
customfishing:
  type: "customfishing_fishing"
  amount: 10                             # number needed
```
