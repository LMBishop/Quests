---
title: Defining items
parent: Configuration
nav_order: 5
---

# Defining items
{: .no_toc }

An **ItemStack** is a **representation of an item** in an inventory.
Every configured ItemStack in Quests is parsed the exact same way. This
page gives guidance on how to define items with specific attributes.

{: .note }
The information on this page describes how to define items across every
configuration file.

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

## Layout

``` yaml
item:
  name: "&6&lSuper Cool Stick"
  item: STICK
  lore: 
   - "&7Really cool lore."
  # field4: value4
  # etc.
```

## Options

| Field                      | Optional        | Minecraft Version | More Information                    |
|----------------------------|-----------------|-------------------|-------------------------------------|
| `item`                     | ❌               | \-                | [Jump](#item)                       |
| `name`                     | ✅ <sup>\*</sup> | \-                | [Jump](#name)                       |
| `lore`                     | ✅               | \-                | [Jump](#lore)                       |
| `enchantments`             | ✅               | \-                | [Jump](#enchantments)               |
| `itemflags`                | ✅               | 1.8+              | [Jump](#item-flags)                 |
| `unbreakable`              | ✅               | 1.8+              | [Jump](#unbreakable)                |
| `attributemodifiers`       | ✅               | 1.13+             | [Jump](#attribute-modifiers)        |
| `custommodeldata`          | ✅               | 1.14+             | [Jump](#custom-model-data)          |
| `custommodeldata-colors`   | ✅               | 1.21.4+           | [Jump](#custom-model-data-colors)   |
| `custommodeldata-flags`    | ✅               | 1.21.4+           | [Jump](#custom-model-data-flags)    |
| `custommodeldata-floats`   | ✅               | 1.21.4+           | [Jump](#custom-model-data-floats)   |
| `custommodeldata-strings`  | ✅               | 1.21.4+           | [Jump](#custom-model-data-strings)  |
| `itemmodel`                | ✅               | 1.21.4+           | [Jump](#item-model)                 |
| `enchantmentglintoverride` | ✅               | 1.20+             | [Jump](#enchantment-glint-override) |
| `hidetooltip`              | ✅               | 1.20+             | [Jump](#hide-tooltip)               |
| `owner-[...]`              | ✅               | 1.8+              | [Jump](#owner)                      |

<sup>\*: The name must be defined for the display item of Quests.</sup>

### Item

  
*`item` or `type` or `material`*

The item is the material the itemstack is made out of. Please see the
[latest
javadocs](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)
(1.13+) or the [1.12
javadocs](https://helpch.at/docs/1.12.2/org/bukkit/Material.html)
(1.8-1.12) for item names. For 1.8-1.12, data codes can be added on at
the end with a colon `:<code>`.

``` yaml
item:
  item: "WHEAT"
  # ...
```

### Name

  
*`name`*

The name is displayed at the top of the item when hovered over, or just
above the hotbar when selected.

``` yaml
item:
  name: "&2&lSuper Cool Name"
  # ...
```

### Lore

  
*`lore`*

The lore is the description of the item seen when hovering over it. You
can remove this omit entirely if a lore is not desired.

``` yaml
item:
  lore:
   - "Line 1"
   - "Line 2"
  # ...
```

### Enchantments

The format of enchantments depends on your Minecraft version.

  
**Pre-1.13**: Use [spigot
names](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html)
-\> format "{enchantment}:{level}"

**1.13+**: Use Vanilla names -\> namespace for vanilla enchantments is
"minecraft" -\> format "{namespace}:{enchantment}:{level}"

``` yaml
item:
  enchantments:
   - "minecraft:infinity:1"
  # ...
```

### Item flags

Item flags can be added to hide enchantment names, etc. A full list of
itemflags is available on the [Spigot
javadocs](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/ItemFlag.html).

``` yaml
item:
  itemflags:
   - "HIDE_ATTRIBUTES"
  # ...
```

### Unbreakable

- *1.8+*'

``` yaml
item:
  unbreakable: true
  # ...
```

### Attribute modifiers

**1.13+** Adds specific attribute modifiers to the items. The UUID
should always be specified otherwise the server will randomly generate
one on each restart. Full list of attributes is available on the [Spigot
javadocs](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/Attribute.html),
along with full list of
[operations](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/AttributeModifier.Operation.html).

``` yaml
item:
  attributemodifiers:
    - attribute: GENERIC_MOVEMENT_SPEED
      modifier:
        uuid: "49dc07dc-bfdb-4dc7-85d3-66ef52b51858"
        name: "generic.movementSpeed"
        operation: ADD_NUMBER
        amount: 0.03
        equipmentslot: HAND
    - attribute: GENERIC_MOVEMENT_SPEED
      modifier:
        uuid: "e22513cf-b15f-4443-9e2f-103c0ff9731b"
        name: "generic.movementSpeed"
        operation: ADD_NUMBER
        amount: 0.01
        equipmentslot: OFF_HAND
  # ...
```

### Custom model data

**1.14+**

``` yaml
item:
  custommodeldata: 12345
  # ...
```

### Custom model data colors

**1.21.4+**

```yaml
item:
  custommodeldata-colors: # use ARGB
  - 4294901760 # red    FFFF0000
  - 4278255360 # green  FF00FF00
  - 4278190335 # blue   FF0000FF
```

### Custom model data flags

**1.21.4+**

```yaml
item:
  custommodeldata-flags:
  - true
  - true
  - false
```

### Custom model data floats

**1.21.4+**

```yaml
item:
  custommodeldata-floats:
  - 3.14
  - 2.7182
  - 1.618033
```

### Custom model data strings

**1.21.4+**

```yaml
item:
  custommodeldata-strings: # hopefully there are no constraints for these lol
  - "the_best_quests_plugin"
  - "WHEEEEEEEN UPDATEEEEEEEEEEEEEE"
  - "https://github.com/LMBishop/Quests/pull/842"
```

### Item model

**1.21.4+**

```yaml
item:
  itemmodel: "namespaced_key_of:the_model"
```

### Enchantment glint override

**1.20+**

```yaml
item:
  enchantmentglintoverride: true
```

### Hide tooltip

**1.20+**

```yaml
item:
  hidetooltip: true
```

### Owner

This only applies if you have a skull item stack (`PLAYER_HEAD` 1.13+,
`SKULL_ITEM` 1.8-1.12). There are three ways to define the player for
the skull: by **username**; **uuid**; or, **base64 encoded string**.

The **preferred method** is to **explicitly specify a base64 encoded
string**. Using any of the other two methods require that the player has
joined the server before, and may possibly make a request to Mojang
(locking the server thread) depending on which server software you use.

You can get the base64 encoded representation of a player skin here:
<https://mineskin.org/>. It will look like the following (may be
referred to as 'texture data'):

    ewogICJ0aW1lc3RhbXAiIDogMTYyNTgzNjU0OTAxNCwKICAicHJvZmlsZUlkIiA6ICJlMmNlNzA0ZWVjNGE0YjE4YTNlYjA4MTRiMzdmYTFkNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJmYXRwaWdzYXJlZmF0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJiMTIzMWEyZjNkYTQ2OTQxZDY1OWI4NDNjZWZhNDljOGE1NTA0ZjE4MzNlOTA3YzY3YmJiMTQ2NTE0OTlhNyIKICAgIH0KICB9Cn0=

You can specify each type by the following:

``` yaml
item:
  owner-base64: "base64 encoded string"
  # ...
```

``` yaml
item:
  owner-username: "username"
  # ...
```

``` yaml
item:
  owner-uuid: "uuid"
  # ...
```

## Quest items

**Quest items** can help simplify your configuration by putting
individual itemstacks inside a named file (under directory items/), to
allow for easy referencing from a task configuration and reducing
configuration duplication across your quests.

The types of quest items are as follows:

- `raw` (items imported using /q a items import)
- `defined` (items manually written following the format above)
- `mmoitems` (items from MMOItems)
- `slimefun` (items from Slimefun)
- `customfishing` (items from CustomFishing)
- `evenmorefish` (items from EvenMoreFish)
- `executableitems` (items from ExecutableItems)
- `itemsadder` (items from ItemsAdder)
- `nexo` (items from Nexo)
- `oraxen` (items from Oraxen)
- `pyrofishingpro` (items from PyroFishingPro)

### Importing items

**Importing** an item means creating a new quest item **from the item
you are holding** in game. To do this, simply hold the desired item and
run `/q a items import <id>`, where `<id>` is the desired name of the
item. Your item will be saved to file items/\<id\>.yml, **with the type
'raw**'.

<img src="https://i.imgur.com/6lsld61.png" height=20>

<img src="https://i.imgur.com/Pg2eO9a.png" height=40>

### Defining items

You can manually define an item by creating a new `yml` file within the
items/ directory. You must specify a `type` and the item itself under
`item`.

#### Defined

**Defined quest items** are regular ItemStacks and follow the format
defined under [§ options](#options).

    items/testitem.yml

``` yaml
type: "defined"
item:
  name: "Cool item"
  type: DIAMOND_SWORD
  lore:
   - "Really cool lore"
```

#### MMOItems

**MMOItems quest items** are ItemStacks which belong to the MMOItems
plugin.

    items/testitem.yml

``` yaml
type: "mmoitems"
item:
  type: "BOW"     #mmoitems type
  id: "HELL_BOW"  #mmoitems id
```

#### Slimefun

**Slimefun quest items** are ItemStacks which belong to the Slimefun
plugin.

    items/testitem.yml

``` yaml
type: "slimefun"
item:
  id: "slimefun_item_id"  #slimefun id
```

#### CustomFishing

**CustomFishing quest items** are ItemStacks which belong to the
CustomFishing plugin.

    items/testitem.yml

``` yaml
type: "customfishing"
item:
  id: "customfishing_id"  #customfishing id
```
or
``` yaml
type: "customfishing"
item:
  ids:  #customfishing ids
  - "customfishing_id_1"
  - "customfishing_id_2"
```

#### EvenMoreFish

**EvenMoreFish quest items** are ItemStacks which belong to the
EvenMoreFish plugin.

    items/testitem.yml

``` yaml
type: "evenmorefish"
item:
  rarity: "evenmorefish_rarity_id"  #evenmorefish rarity id
  fish: "evenmorefish_fish_name"  #evenmorefish fish name
```

#### ExecutableItems

**ExecutableItems quest items** are ItemStacks which belong to the
ExecutableItems plugin.

    items/testitem.yml

``` yaml
type: "executableitems"
item:
  id: "executableitems_id"  #executableitems id
```

#### ItemsAdder

**ItemsAdder quest items** are ItemStacks which belong to the
ItemsAdder plugin.

    items/testitem.yml

``` yaml
type: "itemsadder"
item:
  id: "itemsadder"  #itemsdadder id
```

#### Nexo

**Nexo quest items** are ItemStacks which belong to the
Nexo plugin.

    items/testitem.yml

``` yaml
type: "nexo"
item:
  id: "nexo_id"  #nexo id
```

#### Oraxen

**Oraxen quest items** are ItemStacks which belong to the
Oraxen plugin.

    items/testitem.yml

``` yaml
type: "oraxen"
item:
  id: "oraxen_id"  #oraxen id
```

#### PyroFishingPro

**PyroFishingPro quest items** are ItemStacks which belong to the
PyroFishingPro plugin. Can be used with many task types except `fishing`.
To ensure the orderly functioning, `pyrofishingpro_fishing` type should
be used instead of utilising defined item in regular `fishing` tasks.

    items/testitem.yml

``` yaml
type: "pyrofishingpro"
item:
  fish-number: 123  #pyrofishingpro fish number (optional)
  tier: "Mythical"  #pyrofishing fish tier (optional)
```

### Referencing a quest item

In most cases where an ItemStack is accepted in Quests, you can simply
provide the ID of the quest item under the key `quest-item`.

``` yaml
# Within a task
type: "inventory"
item:
  quest-item: "testitem"
```
