---
title: bedwars1058_generator_collect
parent: External task types
grand_parent: Task types
---

# bedwars1058_generator_collect (task type)

Not released yet (dev builds)
{: .label .label-green }

Plugin 'BedWars1058' required  
{: .label }

Collect a specific item from generators in BedWars1058.

## Options

| Key      | Description                                    | Type    | Required | Default | Notes |
|----------|------------------------------------------------|---------|----------|---------|-------|
| `item`   | The generator item that the player must collect. | String  | Yes      | \-      | \-      |
| `amount` | The amount of items to collect.               | Integer | Yes      | \-      | \-      |

## Examples

Collect 10 diamonds:

``` yaml
bedwars1058:
  type: "bedwars1058_generator_collect"
  item: "DIAMOND"
  amount: 10
```
