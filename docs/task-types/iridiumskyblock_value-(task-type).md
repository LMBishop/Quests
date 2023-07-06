---
title: iridiumskyblock_value
parent: External task types
grand_parent: Task types
---

# iridiumskyblock_value (task type)

Since v3.5
{: .label .label-green }

Plugin 'IridiumSkyblock' required - version 2.x only
{: .label }

Reach a certain IridiumSkyblock value.

## Options

| Key     | Description         | Type    | Required | Default | Notes |
|---------|---------------------|---------|----------|---------|-------|
| `value` | The value to reach. | Integer | Yes      | \-      | \-    |

## Examples

Reach island value 10:

``` yaml
iridiumskyblock:
  type: "iridiumskyblock_value"
  value: 10                             # island level needed
```
