---
title: pinataparty_hit
parent: External task types
grand_parent: Task types
---

# pinaiaparty_hit (task type)

Since v3.15
{: .label .label-green }

Plugin 'PinataParty' required
{: .label }

Hit a PinataParty pinata a set number of times.

## Options

| Key      | Description                                     | Type                | Required | Default | Notes |
|----------|-------------------------------------------------|---------------------|----------|---------|-------|
| `amount` | The number of times to hit a pinata.            | Integer             | Yes      | \-      | \-    |
| `worlds` | Worlds which should count towards the progress. | List of world names | No       | \-      | \-    |

## Examples

Hit 10 pinatas:

``` yaml
pinataparty:
  type: "pinataparty_hit"
  amount: 10                            # amount of pinatas hit
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
