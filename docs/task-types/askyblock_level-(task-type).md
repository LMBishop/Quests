---
title: askyblock_level
parent: External task types
grand_parent: Task types
---

# askyblock_level (task type)

Since v1.7.1
{: .label .label-green }

Plugin 'ASkyBlock' required
{: .label }

Reach a certain ASkyBlock level.

## Options

| Key     | Description         | Type    | Required | Default | Notes |
|---------|---------------------|---------|----------|---------|-------|
| `level` | The level to reach. | Integer | Yes      | \-      | \-    |

## Examples

Reach island level 10:

``` yaml
askyblock:
  type: "askyblock_level"
  level: 10                             # island level needed
```
