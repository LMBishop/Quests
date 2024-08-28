---
title: citizens_deliver
parent: External task types
grand_parent: Task types
---

# citizens_deliver (task type)

Since v2.0.15
{: .label .label-green }

Plugin 'Citizens' required
{: .label }

Deliver a set of items to a Citizens NPC.

## Options

| Key                          | Description                                                      | Type                   | Required | Default | Notes                                                                                                                                                                                                                                                                        |
|------------------------------|------------------------------------------------------------------|------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`                     | The number of items to deliver.                                  | Integer                | Yes      | \-      | \-                                                                                                                                                                                                                                                                           |
| `item`                       | The specific item to deliver.                                    | Material, or ItemStack | Yes      | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `data`                       | The data code for the item.                                      | Integer                | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions.                                                                                                                                                                         |
| `exact-match`                | Whether the item should exactly match what is defined.           | Boolean                | No       | true    | \-                                                                                                                                                                                                                                                                           |
| `remove-items-when-complete` | Whether the items should be taken from the player when complete. | Boolean                | No       | false   | If `allow-partial-completion` is true, then this will also be set to true.                                                                                                                                                                                                   |
| `allow-partial-completion`   | Whether any number of items counts towards the task progress.    | Boolean                | No       | true    | Setting to true will force `remove-items-when-complete` to true as well. If a player obtains any matching item, it will be immediately taken away from them and added towards the quest progress.                                                                            |
| `npc-name`                   | The name of the NPC to deliver to.                               | String                 | No       | \-      | Mutually exclusive with `npc-id`.                                                                                                                                                                                                                                            |
| `npc-id`                     | The id of the NPC to deliver to.                                 | Integer                | No       | \-      | Mutually exclusive with `npc-name`.                                                                                                                                                                                                                                          |
| `worlds`                     | Worlds which should count towards the progress.                  | List of world names    | No       | \-      | \-                                                                                                                                                                                                                                                                           |

## Examples

Deliver 8 of `BEEF` to an NPC named Gerald:

``` yaml
citizensdeliver:
  type: "citizens_deliver"
  npc-name: "Gerald"                    # name of NPC
  # OR npc-id: "npc1"                     ID of NPC (mutally exclusive with npc-name)
  item: BEEF                            # name of item (can be id or minecraft name)
  amount: 8                             # amount of item needed
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  allow-partial-completion: false       # (OPTIONAL) allow partial deliveries - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Deliver 8 of a specific item to an NPC named Gerald:

``` yaml
beef:
  type: "citizens_deliver"
  npc-name: "Gerald"                    # name of NPC
  item:                                 # SPECIFIC item with name and lore
    name: "&cSpecial Beef"
    type: "BEEF"
    lore:
     - "&7This is a special type of beef"
  amount: 8                             # amount of item needed
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  accept-partial-completion: false      # (OPTIONAL) allow partial deliveries - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Deliver 8 of [quest item](../configuration/defining-items#quest-items)
`special_beef` to an NPC named Gerald:

``` yaml
beef:
  type: "citizens_deliver"
  npc-name: "Gerald"                    # name of NPC
  # OR npc-id: "npc1"                     ID of NPC (mutally exclusive with npc-name)
  item:                                 # USING quest-item
    quest-item: "special_beef"
  amount: 8                             # amount of item needed
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  accept-partial-completion: false      # (OPTIONAL) allow partial deliveries - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
