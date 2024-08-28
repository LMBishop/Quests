---
title: resurrecting
parent: Built-in task types
grand_parent: Task types
---

# resurrecting (task type)

Since v3.15.1
{: .label .label-green }

Resurrect a set amount of times (by totem of undying usage).

## Options

| Key      | Description                                     | Type                | Required | Default | Notes |
|----------|-------------------------------------------------|---------------------|----------|---------|-------|
| `amount` | The number of times to resurrect.               | Integer             | Yes      | \-      | \-    |
| `worlds` | Worlds which should count towards the progress. | List of world names | No       | \-      | \-    |

## Examples

Resurrect 5 times:

``` yaml
resurrecting:
  type: "resurrecting"
  amount: 5               # amount of times to resurrect
```
