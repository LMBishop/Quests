---
title: evenmorefish_hunting
parent: External task types
grand_parent: Task types
---

# evenmorefish_hunting (task type)

Since v3.16
{: .label .label-green }

Plugin 'EvenMoreFish' required
{: .label }

Hunt a set amount of EvenMoreFish fish.

## Options

| Key                   | Description                                      | Type                | Required | Default | Notes                                                       |
|-----------------------|--------------------------------------------------|---------------------|----------|---------|-------------------------------------------------------------|
| `amount`              | The number of fish to hunt.                      | Integer             | Yes      | \-      | \-                                                          |
| `rarity` / `rarities` | The specific rarity name(s) to hunt.             | String              | No       | \-      | If this value is not specified, then any rarity will count. |
| `rarity-match-mode`   | The match mode to be used to compare the strings | String              | No       | EQUALS  | One of: `EQUALS`, `STARTS_WITH`, `ENDS_WITH`.               |
| `fish` / `fishes`     | The specific fish name(s) to hunt.               | String              | No       | \-      | If this value is not specified, then any fish will count.   |
| `fish-match-mode`     | The match mode to be used to compare the strings | String              | No       | EQUALS  | One of: `EQUALS`, `STARTS_WITH`, `ENDS_WITH`.               |
| `worlds`              | Worlds which should count towards the progress.  | List of world names | No       | \-      | \-                                                          |

## Examples

Hunt 10 EvenMoreFish fish:

```yaml
evenmorefish:
  type: "evenmorefish_hunting"
  amount: 10                             # number needed
```
