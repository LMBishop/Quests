---
title: znpcsplus_interact
parent: External task types
grand_parent: Task types
---

# znpcsplus_interact (task type)

Since v3.15
{: .label .label-green }

Plugin 'ZNPCsPlus' required
{: .label }

Interact with a ZNPCsPlus NPC.

## Options

| Key        | Description                                     | Type                | Required | Default | Notes                               |
|------------|-------------------------------------------------|---------------------|----------|---------|-------------------------------------|
| `npc-name` | The name of the NPC to interact with.           | String              | No       | \-      | Mutually exclusive with `npc-id`.   |
| `npc-id`   | The id of the NPC to interact with.             | String              | No       | \-      | Mutually exclusive with `npc-name`. |
| `worlds`   | Worlds which should count towards the progress. | List of world names | No       | \-      | \-                                  |

## Examples

Interact with an NPC with ID "gerald":

``` yaml
znpcsplusinteract:
  type: "znpcsplus_interact
  npc-id: "gerald"                      # ID of NPC
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
