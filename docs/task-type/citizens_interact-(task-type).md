  
*This requires the Citizens plugin to activate.*

Interact with a Citizens NPC.

## Options

| Key        | Description                                     | Type                | Required | Default | Notes                               |
|------------|-------------------------------------------------|---------------------|----------|---------|-------------------------------------|
| `npc-name` | The name of the NPC to deliver to.              | Boolean             | No       | \-      | Mutually exclusive with `npc-id`.   |
| `npc-id`   | The id of the NPC to deliver to.                | Boolean             | No       | \-      | Mutually exclusive with `npc-name`. |
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
