---
title: expearn
parent: Built-in task types
grand_parent: Task types
---

# expearn (task type)

Since v2.2
{: .label .label-green }

Earn a set amount of exp after starting the quest.

## Options

| Key      | Description                                     | Type                | Required | Default | Notes |
|----------|-------------------------------------------------|---------------------|----------|---------|-------|
| `amount` | The amount of experience to earn.               | Integer             | Yes      | \-      | \-    |
| `worlds` | Worlds which should count towards the progress. | List of world names | No       | \-      | \-    |

## Examples

Earn 20 experience:

``` yaml
expearn:
  type: "expearn"
  amount: 20                            # amount of experience earned
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
