---
title: fabledskyblock_level
parent: External task types
grand_parent: Task types
---

# fabledskyblock_level (task type)

Since v3.5
{: .label .label-green }

Plugin 'FabledSkyblock' required
{: .label }

Reach a certain FabledSkyblock level.

## Options

| Key     | Description         | Type    | Required | Default | Notes |
|---------|---------------------|---------|----------|---------|-------|
| `level` | The level to reach. | Integer | Yes      | \-      | \-    |

## Examples

Reach island level 10:

``` yaml
fabledskyblock:
  type: "fabledskyblock_level"
  level: 10                             # island level needed
```
