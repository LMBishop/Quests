---
title: superiorskyblock_level
parent: External task types
grand_parent: Task types
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
