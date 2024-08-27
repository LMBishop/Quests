---
title: blockitemdropping
parent: Built-in task types
grand_parent: Task types
---

# blockitemdropping (task type)

Since v3.15
{: .label .label-green }

Drop a certain amount of items from a block. In most cases, the
player must be in survival mode for this to be triggered.

This is triggered by [`BlockDropItemEvent`](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/block/BlockDropItemEvent.html):

> Called if a block broken by a player drops an item. 
> This event will also be called if the player breaks 
> a multi block structure, for example a torch on top 
> of a stone.

## Options

| Key           | Description                                            | Type                           | Required | Default | Notes                                                                                                                                                                                                                                                                           |
|---------------|--------------------------------------------------------|--------------------------------|----------|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`      | The number of items.                                   | Integer                        | Yes      | \-      | \-                                                                                                                                                                                                                                                                              |
| `block`       | The specific blocks to break.                          | Material, or list of materials | No       | \-      | Not specifying this field will allow all blocks to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for block names. |
| `item`        | The specific item to be dropped.                       | Material, or ItemStack         | Yes      | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names.    |
| `data`        | The data code for the item.                            | Integer                        | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions.                                                                                                                                                                            |
| `exact-match` | Whether the item should exactly match what is defined. | Boolean                        | No       | true    | \-                                                                                                                                                                                                                                                                              |
| `worlds`      | Worlds which should count towards the progress.        | List of world names            | No       | \-      | \-                                                                                                                                                                                                                                                                              |

## Examples

Break 8 torches:

``` yaml
torch:
  type: "blockitemdropping"
  item: TORCH                           # name of item (can be id or minecraft name)
  amount: 8                             # amount of item dropped
```
