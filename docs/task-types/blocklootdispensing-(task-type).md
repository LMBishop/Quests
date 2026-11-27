---
title: blocklootdispensing
parent: Built-in task types
grand_parent: Task types
---

# blocklootdispensing (task type)

Not released yet (dev builds)
{: .label .label-green }

Minecraft 1.21.1+ required
{: .label .label-purple }

Make certain block dispense certain loot.

## Options

| Key               | Description                                                           | Type                           | Required | Default | Notes                                                                                                                                                                                                                                                                           |
|-------------------|-----------------------------------------------------------------------|--------------------------------|----------|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`          | The number of items.                                                  | Integer                        | Yes      | \-      | Must be at least 1.                                                                                                                                                                                                                                                             |
| `block`           | The specific blocks to dispense the loot from.                        | Material, or list of materials | No       | \-      | Not specifying this field will allow all blocks to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for block names. |
| `item-requirement` | Item requirement(s) for `item`: a material, an item definition or a list of item definitions (any of them may match). | Item requirement | No | \- | Takes precedence over `item`. See [item requirements](../configuration/item-requirements). |
| `item`            | The specific item to be dispensed.                                    | Material, or ItemStack         | No       | \-      | **Deprecated:** will be removed in a future version, use `item-requirement` instead. Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names. |
| `data`            | The data code for the item.                                           | Integer                        | No       | 0       | **Deprecated:** will be removed in a future version, use `damage` in `item-requirement` instead (for blocks use `MATERIAL:data` in `block`). This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions. Must be between 0 and 32767. |
| `exact-match`     | Whether the item should exactly match what is defined.                | Boolean                        | No       | true    | **Deprecated:** will be removed in a future version, use `item-requirement` instead. |
| `count-dispenses` | Whether the plugin should count dispenses (actions) instead of items. | Boolean                        | No       | false   | \-                                                                                                                                                                                                                                                                              |
| `worlds`          | Worlds which should count towards the progress.                       | List of world names            | No       | \-      | \-                                                                                                                                                                                                                                                                              |
| `ignored-worlds`  | Worlds which should not count towards the progress.                   | List of world names            | No       | \-      | \-                                                                                                                                                                                                                                                                              |
