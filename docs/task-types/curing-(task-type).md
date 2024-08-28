---
title: curing
parent: Built-in task types
grand_parent: Task types
---

# curing (task type)

Since v3.15.1
{: .label .label-green }

Cure a set amount of zombie villagers.

## Options

| Key      | Description                                     | Type                | Required | Default | Notes |
|----------|-------------------------------------------------|---------------------|----------|---------|-------|
| `amount` | The number of zombie villagers to cure.         | Integer             | Yes      | \-      | \-    |
| `worlds` | Worlds which should count towards the progress. | List of world names | No       | \-      | \-    |

## Examples

Cure 8 zombie villagers:

``` yaml
curing:
  type: "curing"
  amount: 8               # amount of times to cure a zombie villager
```
