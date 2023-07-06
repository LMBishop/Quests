---
title: superiorskyblock_worth
parent: External task types
grand_parent: Task types
---

# superiorskyblock_worth (task type)

Since v3.7
{: .label .label-green }

Plugin 'SuperiorSkyblock' required
{: .label }

Reach a certain SuperiorSkyblock worth.

## Options

| Key     | Description         | Type    | Required | Default | Notes |
|---------|---------------------|---------|----------|---------|-------|
| `worth` | The worth to reach. | Integer | Yes      | \-      | \-    |

## Examples

Reach island worth 100:

``` yaml
superiorskyblock:
  type: "superiorskyblock_worth"
  level: 100                             # island worth needed
```
