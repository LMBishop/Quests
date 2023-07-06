---
title: superiorskyblock_level
parent: Task types
nav_order: 39
---

# superiorskyblock_level (task type)

Since v3.7
{: .label .label-green }

Plugin 'SuperiorSkyblock' required
{: .label }

Reach a certain SuperiorSkyblock level.

## Options

| Key     | Description         | Type    | Required | Default | Notes |
|---------|---------------------|---------|----------|---------|-------|
| `level` | The level to reach. | Integer | Yes      | \-      | \-    |

## Examples

Reach island level 10:

``` yaml
superiorskyblock:
  type: "superiorskyblock_level"
  level: 10                             # island level needed
```
