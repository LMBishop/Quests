---
title: evenmorefish_fishing
parent: External task types
grand_parent: Task types
---

# evenmorefish_fishing (task type)

Since v3.16
{: .label .label-green }

Plugin 'EvenMoreFish' required
{: .label }

Catch a set amount of EvenMoreFish fish.

## Options

| Key                   | Description                                      | Type                | Required | Default | Notes                                                       |
|-----------------------|--------------------------------------------------|---------------------|----------|---------|-------------------------------------------------------------|
| `amount`              | The number of fish to catch.                     | Integer             | Yes      | \-      | \-                                                          |
| `rarity` / `rarities` | The specific rarity name(s) to catch.            | String              | No       | \-      | If this value is not specified, then any rarity will count. |
| `rarity-match-mode`   | The match mode to be used to compare the strings | String              | No       | EQUALS  | One of: `EQUALS`, `STARTS_WITH`, `ENDS_WITH`.               |
| `fish` / `fishes`     | The specific fish name(s) to catch.              | String              | No       | \-      | If this value is not specified, then any fish will count.   |
| `fish-match-mode`     | The match mode to be used to compare the strings | String              | No       | EQUALS  | One of: `EQUALS`, `STARTS_WITH`, `ENDS_WITH`.               |
| `worlds`              | Worlds which should count towards the progress.  | List of world names | No       | \-      | \-                                                          |

## Examples

Catch 10 EvenMoreFish fish:

```yaml
evenmorefish:
  type: "evenmorefish_fishing"
  amount: 10                             # number needed
```
