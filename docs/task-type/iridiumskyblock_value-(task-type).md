---
title: iridiumskyblock_value
parent: Task types
nav_order: 34
---

# iridiumskyblock_value (task type)

Since v3.5
{: .label .label-green }

Plugin 'IridiumSkyblock' required - version 2.x only
{: .label }

Reach a certain IridiumSkyblock value.

## Options

| Key     | Description        | Type    | Required | Default | Notes |
|---------|--------------------|---------|----------|---------|-------|
| `value` | The valueto reach. | Integer | Yes      | \-      | \-    |

## Examples

Reach island value 10:

``` yaml
iridiumskyblock:
  type: "iridiumskyblock_value"
  value: 10                             # island level needed
```
