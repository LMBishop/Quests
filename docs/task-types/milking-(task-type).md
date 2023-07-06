---
title: milking
parent: Built-in task types
grand_parent: Task types
---

# milking (task type)

Since v2.0
{: .label .label-green }

Milk a set amount of cows.

## Options

| Key      | Description                                     | Type                | Required | Default | Notes |
|----------|-------------------------------------------------|---------------------|----------|---------|-------|
| `amount` | The number of cows to milk.                     | Integer             | Yes      | \-      | \-    |
| `worlds` | Worlds which should count towards the progress. | List of world names | No       | \-      | \-    |

## Examples

Milk 10 cows:

``` yaml
milking:
  type: "milking"
  amount: 10                            # amount of cows milked
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
