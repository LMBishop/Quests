---
title: citizens_interact
parent: External task types
grand_parent: Task types
---

# citizens_interact (task type)

Since v2.0.15
{: .label .label-green }

Plugin 'Citizens' required
{: .label }

Interact with a Citizens NPC.

## Options

| Key        | Description                                     | Type                | Required | Default | Notes                               |
|------------|-------------------------------------------------|---------------------|----------|---------|-------------------------------------|
| `npc-name` | The name of the NPC to interact with.           | String              | No       | \-      | Mutually exclusive with `npc-id`.   |
| `npc-id`   | The id of the NPC to interact with.             | Integer             | No       | \-      | Mutually exclusive with `npc-name`. |
| `worlds`   | Worlds which should count towards the progress. | List of world names | No       | \-      | \-                                  |

## Examples

Interact with an NPC named Gerald:

``` yaml
citizensinteract:
  type: "citizens_interact"
  npc-name: "Gerald"                    # name of NPC
  # OR npc-id: "npc1"                     ID of NPC (mutally exclusive with npc-name)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
