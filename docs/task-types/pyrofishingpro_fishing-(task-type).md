---
title: pyrofishingpro_fishing
parent: External task types
grand_parent: Task types
---

# pyrofishingpro_fishing (task type)

Since v3.15
{: .label .label-green }

Plugin 'PyroFishingPro' required
{: .label }

Catch a set amount of a Pyro fish from the sea.

## Options

| Key           | Description                                     | Type                | Required | Default | Notes                                                             |
|---------------|-------------------------------------------------|---------------------|----------|---------|-------------------------------------------------------------------|
| `amount`      | The number of Pyro fish to catch.               | Integer             | Yes      | \-      | \-                                                                |
| `fish-number` | The specific fish number to catch.              | Integer             | No       | \-      | If this value is not specified, then any fish will count.         |
| `tier`        | The specific tier of fish to catch.             | String              | No       | \-      | If this value is not specified, then any tier of fish will count. |
| `worlds`      | Worlds which should count towards the progress. | List of world names | No       | \-      | \-                                                                |

## Examples

Catch 10 Pyro fish:

```yaml
pyrofishing:
  type: "pyrofishingpro_fishing"
  amount: 10                             # number needed
```
