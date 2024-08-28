---
title: customfishing_fishing
parent: External task types
grand_parent: Task types
---

# customfishing_fishing (task type)

Since v3.15.1
{: .label .label-green }

Plugin 'CustomFishing' required
{: .label }

Catch a set amount of CustomFishing loots.

## Options

| Key                | Description                                      | Type                | Required | Default | Notes                                                              |
|--------------------|--------------------------------------------------|---------------------|----------|---------|--------------------------------------------------------------------|
| `amount`           | The number of loots to catch.                    | Integer             | Yes      | \-      | \-                                                                 |
| `loot` / `loots`   | The specific loot id(s) to catch.                | String              | No       | \-      | If this value is not specified, then any loot will count.          |
| `loot-match-mode`  | The match mode to be used to compare the strings | String              | No       | EQUALS  | One of: `EQUALS`, `STARTS_WITH`, `ENDS_WITH`.                      |
| `group` / `groups` | The specific group of loot to catch.             | String              | No       | \-      | If this value is not specified, then any group of loot will count. |
| `group-match-mode` | The match mode to be used to compare the strings | String              | No       | EQUALS  | One of: `EQUALS`, `STARTS_WITH`, `ENDS_WITH`.                      |
| `worlds`           | Worlds which should count towards the progress.  | List of world names | No       | \-      | \-                                                                 |

## Examples

Catch 10 CustomFishing loots:

```yaml
customfishing:
  type: "customfishing_fishing"
  amount: 10                             # number needed
```
