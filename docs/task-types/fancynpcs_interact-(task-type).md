---
title: fancynpcs_interact
parent: External task types
grand_parent: Task types
---

# fancynpcs_interact (task type)

Since v3.16
{: .label .label-green }

Plugin 'FancyNpcs' required
{: .label }

Interact with a FancyNpcs NPC.

## Options

| Key        | Description                                     | Type                | Required | Default | Notes                               |
|------------|-------------------------------------------------|---------------------|----------|---------|-------------------------------------|
| `npc-name` | The name of the NPC to interact with.           | String              | No       | \-      | Mutually exclusive with `npc-id`.   |
| `npc-id`   | The id of the NPC to interact with.             | String              | No       | \-      | Mutually exclusive with `npc-name`. |
| `worlds`   | Worlds which should count towards the progress. | List of world names | No       | \-      | \-                                  |

## Examples

Interact with an NPC with ID "gerald":

``` yaml
fancynpcsinteract:
  type: "fancynpcs_interact
  npc-id: "gerald"                      # ID of NPC
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
