---
title: fishing
parent: Built-in task types
grand_parent: Task types
---

# fishing (task type)

Since v2.0
{: .label .label-green }

Fish a set amount of items.

{: .note }
Since Quests v3.13, `fishingcertain` and `fishing` have been merged into
one. Both names can be used to refer to this task.

## Options

| Key           | Description                                            | Type                | Required | Default | Notes                                                                |
|---------------|--------------------------------------------------------|---------------------|----------|---------|----------------------------------------------------------------------|
| `amount`      | The number of fish to catch.                           | Integer             | Yes      | \-      | \-                                                                   |
| `item`        | Specific item which should be used to kill mobs.       | ItemStack           | No       | \-      | Accepts standard [item definition](../configuration/defining-items). |
| `worlds`      | Worlds which should count towards the progress.        | List of world names | No       | \-      | \-                                                                   |
| `exact-match` | Whether the item should exactly match what is defined. | Boolean             | No       | true    | \-                                                                   |

## Examples

Fish 10 of any item:

``` yaml
fishing:
  type: "fishing"
  amount: 10                            # amount of fish caught
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Fish 10 pufferfish:

``` yaml
fishingcertain:
  type: "fishingcertain"
  item: PUFFERFISH                      # type of item caught
  amount: 10                            # amount of item caught
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Fish 10 of [quest item](../configuration/defining-items#quest-items)
`super_fish`:

``` yaml
fishingcertain:
  type: "fishingcertain"
  item:                                 # type of item caught
    quest-item: "super_fish"
  amount: 10                            # amount of item caught
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
