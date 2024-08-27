---
title: interact
parent: Built-in task types
grand_parent: Task types
---

# interact (task type)

Since v3.13.2
{: .label .label-green }

Interact with an item a certain amount of times.

## Options

| Key                                                            | Description                                               | Type                          | Required | Default | Notes                                                                                                                                                                                                                                                                              |
|----------------------------------------------------------------|-----------------------------------------------------------|-------------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`                                                       | The number of interactions.                               | Integer                       | Yes      | \-      | \-                                                                                                                                                                                                                                                                                 |
| `item`                                                         | The specific item to be used while interacting.           | Material, or ItemStack        | No       | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names.       |
| `data`                                                         | The data code for the item.                               | Integer                       | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions.                                                                                                                                                                               |
| `exact-match`                                                  | Whether the item should exactly match what is defined.    | Boolean                       | No       | true    | \-                                                                                                                                                                                                                                                                                 |
| `block` / `blocks`                                             | The specific block(s) to interact with.                   | Material, or list of material | No       | \-      | Not specifying this field will allow all blocks to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `action` / `actions`                                           | The specific interact action(s).                          | Action, or list of actions    | No       | \-      | Not specifying this field will allow all actions to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/block/Action.html) for action names.                                                                                  |
| `hand` / `hands`                                               | The specific hand(s) to interact with.                    | Hand, or list of hands        | No       | \-      | Not specifying this field will allow all hands to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/inventory/EquipmentSlot.html) for hand names.                                                                                 |
| `use-interacted-block-result` / `use-interacted-block-results` | The specific block use result(s) of the interaction event | Result, or list of results    | No       | \-      | Not specifying this field will allow all results except DENY to count towards the task (in most setups it's the proper option).                                                                                                                                                    |
| `use-item-in-hand-result` / `use-item-in-hand-results`         | The specific item use result(s) of the interaction event  | Result, or list of results    | No       | \-      | Not specifying this field will allow all results to count towards the task (in most setups it's the proper option).                                                                                                                                                                |
| `worlds`                                                       | Worlds which should count towards the progress.           | List of world names           | No       | \-      | \-                                                                                                                                                                                                                                                                                 |

## Examples

Right click sand with a diamond pickaxe 100 times:

``` yaml
interact_example_task:
  type: "interact"
  block: "SAND"                         # (OPTIONAL) block to be clicked
  action: RIGHT_CLICK_BLOCK             # (OPTIONAL) the action the player have to perform with the item and block
  item: DIAMOND_PICKAXE                 # (OPTIONAL) the item player have to interact with the sand with
  exact-match: false                    # (OPTIONAL) ignore diamond pickaxe nbt
  amount: 100                           # amount of interactions needed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
