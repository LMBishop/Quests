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
| `amount`          | The number of items.                                                  | Integer                        | Yes      | \-      | \-                                                                                                                                                                                                                                                                              |
| `block`           | The specific blocks to dispense the loot from.                        | Material, or list of materials | No       | \-      | Not specifying this field will allow all blocks to count towards the task. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for block names. |
| `item`            | The specific item to be dispensed.                                    | Material, or ItemStack         | No       | \-      | Accepts standard [item definition](../configuration/defining-items). Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for material names.    |
| `data`            | The data code for the item.                                           | Integer                        | No       | 0       | This field is not used in Minecraft versions 1.13+, nor is it compatible with ItemStack definitions.                                                                                                                                                                            |
| `exact-match`     | Whether the item should exactly match what is defined.                | Boolean                        | No       | true    | \-                                                                                                                                                                                                                                                                              |
| `count-dispenses` | Whether the plugin should count dispenses (actions) instead of items. | Boolean                        | No       | false   | \-                                                                                                                                                                                                                                                                              |
| `worlds`          | Worlds which should count towards the progress.                       | List of world names            | No       | \-      | \-                                                                                                                                                                                                                                                                              |
