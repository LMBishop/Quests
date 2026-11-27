---
title: Item requirements
parent: Configuration
nav_order: 5.5
---

# Item requirements
{: .no_toc }

An **item requirement** describes which items count for a task, for example which item a player
must hold while breaking a block, or which items count towards an `inventory` task. Unlike an
[item definition](defining-items), which creates one item, a requirement can match many items: it
can check only some properties, match text partially and list alternatives.

Every task type option accepting an item has a `-requirement` counterpart, for example `item` and
`item-requirement`, or `projectile-item` and `projectile-item-requirement`. If both are set, the
`-requirement` option is used and the older options are reported as ignored.

{: .warning }
The older item options (`item`, `projectile-item`, `ingredient`, ...), the `data` option and their
`exact-match` options are **deprecated** and will be removed in a future version; using them shows a
[configuration warning](configuration-problems) when quests are loaded. Use the `-requirement`
options instead: `exact-match: false` corresponds to `type: defined` with only `material`, and
`exact-match: true` to `type: defined-exact`, and `data` to the `damage` option (for blocks, use
`block: MATERIAL:data` instead).

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

## Forms

An item requirement can be written in four forms, from the shortest to the most flexible:

| Form                         | Example                                          | Matches                                        |
|------------------------------|--------------------------------------------------|------------------------------------------------|
| a material                   | `item-requirement: DIAMOND`                      | any diamond                                    |
| an [item tag](#item-tags)    | `item-requirement: "#minecraft:logs"`            | any log or wood                                |
| an item definition (section) | `item-requirement: {material: DIAMOND_SWORD, name: "&6Excalibur"}` | items with **all** the given properties |
| a list of any of the above   | `item-requirement: [DIAMOND, EMERALD]`           | items matching **any** entry of the list       |

A material or a tag on its own is a shorthand for a definition with only `material`
(`{type: defined, material: DIAMOND}`).

### A material

``` yaml
tasks:
  mine-diamonds:
    type: blockbreak
    block: DIAMOND_ORE
    item-requirement: DIAMOND_PICKAXE      # must be mined with a diamond pickaxe
    amount: 10
```
Any diamond pickaxe counts, whatever its name, lore or enchantments.

### An item tag

``` yaml
tasks:
  collect-logs:
    type: inventory
    item-requirement: "#minecraft:logs"    # quoted: # starts a comment in YAML
    amount: 64
```
Oak, birch, spruce, ... logs and wood all count.

### An item definition

A section checks several properties at once; **all** of them must match. Properties which are not
listed are not checked.

``` yaml
tasks:
  slay-with-excalibur:
    type: mobkilling
    mob: ZOMBIE
    item-requirement:
      type: defined                        # the default, may be omitted
      material: [DIAMOND_SWORD, NETHERITE_SWORD]
      name: "&6Excalibur"
      enchantments: [SHARPNESS:3+, MENDING:0]
    amount: 20
```
Counts kills with a diamond or netherite sword named `§6Excalibur`, with at least Sharpness III and
without Mending. Its lore, durability and other enchantments do not matter.

| Held item                                              | Counts | Why                     |
|--------------------------------------------------------|--------|-------------------------|
| Netherite sword "&6Excalibur", Sharpness V, Unbreaking | yes    |                         |
| Diamond sword "&6Excalibur", Sharpness III             | yes    |                         |
| Diamond sword "&6Excalibur", Sharpness V, Mending      | no     | Mending is forbidden    |
| Diamond sword "Excalibur" (no colour), Sharpness V     | no     | different name          |
| Iron sword "&6Excalibur", Sharpness V                  | no     | wrong material          |

### An exact item

With `type: defined-exact` the item must be **exactly** the item described, including everything
the definition does not mention: an extra enchantment, lore line or other item data makes it a
different item. Only the amount is ignored.

``` yaml
tasks:
  deliver-crown:
    type: citizens_deliver
    npc-name: "King"
    item-requirement:
      type: defined-exact
      material: GOLDEN_HELMET
      name: "&eCrown"
      lore: ["&7Worn by kings"]
    amount: 1
```
Only a golden helmet with exactly this name and lore, and nothing else (no enchantments, no damage)
is accepted. See [defined-exact](#defined-exact) for what the options may contain.

### A list

A list accepts an item if it matches **any** of its entries. Entries can use any of the forms above,
mixed freely.

``` yaml
tasks:
  bring-gems:
    type: inventory
    item-requirement:
      - DIAMOND                            # any diamond
      - EMERALD                            # or any emerald
      - material: AMETHYST_SHARD           # or an amethyst shard named "Polished Shard"
        name: "&dPolished Shard"
      - type: defined-exact                # or exactly this ender chest
        material: ENDER_CHEST
        name: "&5Treasure"
    amount: 32
```
Items matching different entries count together: 20 diamonds and 12 emeralds complete the task.

### Other item options

Task types with several item options have a `-requirement` counterpart for each of them, written
in the same forms:

``` yaml
tasks:
  snipe-skeletons:
    type: mobkilling
    mob: SKELETON
    item-requirement:                      # the bow the arrow was shot from
      material: BOW
      enchantments: POWER:4+
    projectile-item-requirement: SPECTRAL_ARROW   # the arrow itself
    amount: 15

  brew-with-glistering-melon:
    type: brewing
    ingredient-requirement: GLISTERING_MELON_SLICE
    amount: 3
```
See the page of each [task type](../task-types) for its item options.

## Types

| Type            | Matches                                                                                                                                                              |
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `defined`       | Items satisfying **every configured option**. Options which are not configured are not checked. This is the default when `type` is omitted.                          |
| `defined-exact` | Items whose full item data (everything except the amount) is **the same as the item built from the definition**. `material` is required, and every option must describe exactly one value (see below). |

## How conditions combine

```
item-requirement   list of definitions    → OR   the item matches any of the definitions
  definition       its options            → AND  every option of the definition must match
    option         list of values / sets  → OR   (see "A list means" below)
      set          mode + conditions      →      contains-all, contains-any, exact (, ordered, none)
        condition  one enchantment / item flag / lore line
```

## Options

Options are registered when the plugin starts, depending on the server version. An option the
server does not support is reported as an unknown option (a warning) and ignored; the warning lists
every option available on the server.

| Option                       | Value                                                                              | A list means                         | Server version    |
|------------------------------|------------------------------------------------------------------------------------|--------------------------------------|-------------------|
| `material`                   | Material name, e.g. `STONE`, or an [item tag](#item-tags), e.g. `#minecraft:logs`  | any of                               | all (tags: 1.13+) |
| `damage`                     | Data value (1.8-1.12) or durability damage, at least 0                             | any of                               | all               |
| `name`                       | [Text matcher](#text-matchers)                                                     | any of                               | all               |
| `lore`                       | [Lore conditions](#lore)                                                           | one set, or a list of sets (any of)  | all               |
| `enchantments`               | [Enchantment conditions](#enchantments)                                            | one set, or a list of sets (any of)  | all               |
| `item-flags`                 | [Item flag conditions](#item-flags)                                                | one set, or a list of sets (any of)  | 1.8+              |
| `unbreakable`                | `true` or `false`                                                                  | \-                                   | 1.11+             |
| `custom-model-data`          | Integer                                                                            | any of                               | 1.14+             |
| `enchantment-glint-override` | `true` or `false`                                                                  | \-                                   | 1.20.5+           |
| `hide-tooltip`               | `true` or `false`                                                                  | \-                                   | 1.20.5+           |
| `item-model`                 | Namespaced key, e.g. `minecraft:diamond`                                           | any of                               | 1.21.4+           |

An item has one material, one name and so on, so for these **single-valued** options a list simply
means "any of the values":

``` yaml
item-requirement:
  material: [DIAMOND_SWORD, NETHERITE_SWORD]   # either sword
  custom-model-data: [1001, 1002]              # either model
```

`lore`, `enchantments` and `item-flags` describe **collections** (an item has many lore lines,
enchantments and flags), so their values are [sets of conditions](#sets-and-modes) instead.

Invalid values (unknown materials, enchantments, levels, ...) are errors, so a requirement never
silently matches more or fewer items than configured.

## Item tags

Instead of a material, `material` accepts an **item tag**, written like in Minecraft commands: `#`
followed by the tag key. The `minecraft:` namespace may be omitted.

``` yaml
item-requirement:
  material: "#minecraft:logs"        # any log or wood
item-requirement:
  material:                          # any of: a tag or specific materials
    - "#swords"                      # same as #minecraft:swords
    - TRIDENT
```

Any item tag known to the server can be used: the [vanilla item tags](https://minecraft.wiki/w/Tag#Item_tags_2)
of the server version and item tags added by data packs. Quote the value, as `#` starts a comment
in YAML.

* Item tags require Minecraft **1.13** or newer; on older servers a tag is a configuration error.
* The set of available tags depends on the server version; an unknown tag is a configuration error.
* Only item tags are supported. Block tags (such as `minecraft:mineable/pickaxe`) describe placed
  blocks and do not cover items which are not blocks.
* A tag matches several materials, so it cannot be used with `defined-exact`.
* Tags are looked up when quests are loaded; after changing data packs, reload Quests with
  `/q a reload`.

## Text matchers

A text matcher checks one text, such as the item name or a single lore line.
[Colour codes](colour-codes) are translated before comparing.

``` yaml
name: "&6Sword"              # exactly "§6Sword" (case sensitive)
name: ""                     # the item has a name, and it is empty
name:
  mode: absent               # the item has no name
name:
  mode: present              # the item has any name
name:
  text: "sword"
  mode: contains             # equals (default), starts-with, contains, ends-with
  ignore-case: true          # default: false
name:                        # a list: any of the names
  - "&6Sword"
  - text: "blade"
    mode: ends-with
```

| Mode          | Matches                                         |
|---------------|-------------------------------------------------|
| `equals`      | the whole text is equal to `text` (default)     |
| `starts-with` | the text starts with `text`                     |
| `contains`    | the text contains `text`                        |
| `ends-with`   | the text ends with `text`                       |
| `present`     | any text, including an empty one (no `text`)    |
| `absent`      | no text at all (no `text`)                      |

{: .note }
`name: ~` (YAML null) cannot be used to require "no name": the server removes such options when it
reads the file, so the requirement would not check the name at all. Use `mode: absent` instead.

## Sets and modes

`enchantments`, `item-flags` and `lore` are written as **sets** of **conditions**. A set has a
**mode** telling how its conditions are applied to the item. An option can list several sets as
alternatives: the option matches if **any** of its sets matches.

The shape of the value decides what it means:

| Shape                                               | Meaning                               |
|-----------------------------------------------------|---------------------------------------|
| a single condition: `SHARPNESS:5`                   | one set with one condition            |
| a flat list of conditions: `[SHARPNESS:5, UNBREAKING]` | one set, default mode              |
| a section: `{mode: exact, values: [...]}` (lore: `lines`) | one set with a mode             |
| a list of lists / sections                          | several sets, **any of them** may match |
| `[]`                                                | the item has none (in every mode)     |

A list mixing conditions and sets is an error, because it is unclear whether it is one set or
several; write either `[A, B]` (both) or `[[A], [B]]` (either). Within one set every enchantment or
item flag may appear only once; in different sets it may appear freely.

``` yaml
enchantments: SHARPNESS:5                    # one set, one condition
enchantments: [SHARPNESS:5, UNBREAKING]      # one set, mode contains-all
enchantments:                                # one set with a mode
  mode: exact
  values: [SHARPNESS:5, UNBREAKING]
enchantments:                                # two sets: either of them
  - [SHARPNESS:5, UNBREAKING]
  - mode: contains-any
    values: [FIRE_ASPECT, KNOCKBACK]
enchantments: []                             # no enchantments
```

## Enchantments

Each condition is an enchantment with an optional level. The level of an enchantment the item does
not have counts as **0**, which makes it possible to require that an enchantment is absent.

| Condition        | The level on the item must be | Meaning                                   |
|------------------|-------------------------------|-------------------------------------------|
| `SHARPNESS`      | 1 or higher                   | has Sharpness, any level                  |
| `SHARPNESS:3`    | exactly 3                     |                                           |
| `SHARPNESS:3+`   | 3 or higher                   |                                           |
| `SHARPNESS:3-`   | 0 to 3                        | at most III — **not having it counts**    |
| `SHARPNESS:1-3`  | 1 to 3                        | at most III, and it must be there         |
| `SHARPNESS:3-5`  | 3 to 5                        |                                           |
| `SHARPNESS:0`    | 0                             | does **not** have Sharpness               |

Enchantments can also be given as namespaced keys (`minecraft:sharpness:3+`) on servers supporting
them. Enchantments stored in enchanted books count as well.

| Mode                     | A set matches if                                                                   |
|--------------------------|------------------------------------------------------------------------------------|
| `contains-all` (default) | every condition holds; the item may have other enchantments                        |
| `contains-any`           | at least one condition holds                                                       |
| `exact`                  | every condition holds, and the item has no enchantments other than those listed    |

There is no `none` mode for enchantments: "none of these" is written with level 0, e.g.
`[MENDING:0, VANISHING_CURSE:0]`, or with an upper bound, e.g. `SHARPNESS:2-`.

### Examples

**Has these enchantments, others do not matter**

``` yaml
enchantments: [SHARPNESS:4+, UNBREAKING]
```
Sharpness IV or higher **and** any Unbreaking.

| Item                                 | Matches | Why                       |
|--------------------------------------|---------|---------------------------|
| Sharpness V, Unbreaking I            | yes     |                           |
| Sharpness IV, Unbreaking III, Mending | yes    | other enchantments are fine |
| Sharpness III, Unbreaking III        | no      | Sharpness below IV        |
| Sharpness V                          | no      | no Unbreaking             |

**Required and forbidden enchantments in one set**

``` yaml
enchantments: [SHARPNESS:5, MENDING:0, VANISHING_CURSE:0]
```
Sharpness V **and** no Mending **and** no Curse of Vanishing.

| Item                       | Matches |
|----------------------------|---------|
| Sharpness V                | yes     |
| Sharpness V, Unbreaking III | yes    |
| Sharpness V, Mending       | no      |
| Sharpness IV               | no      |

**At most a level (not having it counts)**

``` yaml
enchantments: [SHARPNESS:2-, SMITE:2-, BANE_OF_ARTHROPODS:2-]
```
Each of them at most II, or not at all — "a weak sword".

| Item                  | Matches |
|-----------------------|---------|
| no enchantments       | yes     |
| Sharpness II, Smite I | yes     |
| Unbreaking III        | yes     |
| Sharpness III         | no      |

**Any of these** (`contains-any`)

``` yaml
enchantments:
  mode: contains-any
  values: [LOOTING:2+, FORTUNE:2+, SILK_TOUCH]
```
Looting II+ **or** Fortune II+ **or** Silk Touch.

| Item                    | Matches |
|-------------------------|---------|
| Fortune III             | yes     |
| Silk Touch, Efficiency V | yes    |
| Looting I               | no      |
| Efficiency V            | no      |

**Exactly these, nothing else** (`exact`)

``` yaml
enchantments:
  mode: exact
  values: [EFFICIENCY:4+, UNBREAKING:3]
```
Efficiency IV+ **and** Unbreaking III **and** no other enchantments.

| Item                                  | Matches | Why                       |
|---------------------------------------|---------|---------------------------|
| Efficiency V, Unbreaking III          | yes     |                           |
| Efficiency IV, Unbreaking III         | yes     |                           |
| Efficiency V, Unbreaking III, Mending | no      | Mending is not listed     |
| Efficiency V                          | no      | no Unbreaking III         |

**Alternative sets** (a list of sets)

``` yaml
enchantments:
  - [SHARPNESS:5, FIRE_ASPECT:2]   # set A
  - [SMITE:5, LOOTING:3]           # set B
```
(Sharpness V **and** Fire Aspect II) **or** (Smite V **and** Looting III).

| Item                                   | Matches | Why                                   |
|----------------------------------------|---------|---------------------------------------|
| Sharpness V, Fire Aspect II, Looting III | yes   | set A                                 |
| Smite V, Looting III                   | yes     | set B                                 |
| Sharpness V, Looting III               | no      | A: no Fire Aspect; B: no Smite        |
| Smite V, Fire Aspect II                | no      | A: no Sharpness; B: no Looting        |

**Alternative sets with different modes**

``` yaml
enchantments:
  - []                                       # set A: no enchantments
  - mode: exact                              # set B: only Unbreaking (any level)
    values: [UNBREAKING]
  - mode: contains-any                       # set C: any curse, anything else allowed
    values: [BINDING_CURSE, VANISHING_CURSE]
```

| Item                            | Matches | Why                                                   |
|---------------------------------|---------|-------------------------------------------------------|
| no enchantments                 | yes     | set A                                                 |
| Unbreaking II                   | yes     | set B                                                 |
| Unbreaking II, Mending          | no      | A: has enchantments; B: Mending not listed; C: no curse |
| Sharpness V, Curse of Vanishing | yes     | set C                                                 |

**Errors**

``` yaml
enchantments:                  # error: mixes conditions and sets
  - SHARPNESS:5
  - [FIRE_ASPECT:2]
enchantments: [SHARPNESS:3+, SHARPNESS:5-]   # error: Sharpness twice in one set, use SHARPNESS:3-5
enchantments: SHARPNESS:5-3    # error: reversed range
```

## Item flags

Each condition is an [item flag](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/inventory/ItemFlag.html)
which must be set. Item flags have no levels, so "must not be set" is written with mode `none`.

| Mode                     | A set matches if                                            |
|--------------------------|-------------------------------------------------------------|
| `contains-all` (default) | every listed flag is set; other flags may be set            |
| `contains-any`           | at least one listed flag is set                             |
| `exact`                  | exactly the listed flags are set                            |
| `none`                   | none of the listed flags is set                             |

``` yaml
item-flags: HIDE_ENCHANTS                     # HIDE_ENCHANTS is set
item-flags:
  mode: none
  values: [HIDE_ENCHANTS, HIDE_ATTRIBUTES]   # neither is set
item-flags: []                                # no flags at all
```

## Lore

A lore requirement compares **lore lines** (the lines of text on the item) with **line conditions**
(the entries in the configuration). Every line condition is a [text matcher](#text-matchers)
describing **one** lore line, with its own mode and `ignore-case`.

| Mode               | A set matches if                                                                                                     |
|--------------------|----------------------------------------------------------------------------------------------------------------------|
| `exact` (default)  | the lore has as many lines as there are conditions, and lore line *n* satisfies condition *n*                        |
| `ordered`          | every condition is satisfied by a **different** lore line, in the order of the conditions; other lines may appear before, between and after them |
| `contains-all`     | every condition is satisfied by a **different** lore line, in any order; other lines may appear                       |
| `contains-any`     | at least one lore line satisfies at least one condition                                                              |
| `none`             | no lore line satisfies any condition                                                                                 |

The conditions of a set are listed under `lines` (instead of `values`). A plain list of lines is one
set in mode `exact`; `lore: []` means "no lore".

### Examples

The examples use these items (colours left out):

| Item | Lore lines                                   |
|------|----------------------------------------------|
| X    | 1: `Rare`, 2: `Legendary sword`, 3: `Soulbound` |
| Y    | 1: `Legendary sword`, 2: `Rare`              |
| Z    | no lore                                      |
| W    | 1: `Common`, 2: `[Quest Item]`               |

**Exactly this lore** (`exact`, the default)

``` yaml
lore: ["Rare", "Legendary sword", "Soulbound"]
```
Exactly 3 lore lines: line 1 is `Rare`, line 2 is `Legendary sword`, line 3 is `Soulbound`.

X yes · Y no (2 lines, different order) · Z no · W no

**Lines in this order** (`ordered`)

``` yaml
lore:
  mode: ordered
  lines:
    - "Rare"                          # condition A
    - text: "sword"                   # condition B
      mode: ends-with
```
A lore line `Rare`, and **after it** a lore line ending with `sword`.

X yes (A → line 1, B → line 2) · Y no (`sword` comes before `Rare`) · Z no · W no

**These lines, in any order** (`contains-all`)

``` yaml
lore:
  mode: contains-all
  lines: ["Rare", {text: "sword", mode: ends-with}]
```

X yes · Y yes · Z no · W no

**One lore line cannot satisfy two conditions** (`contains-all`)

``` yaml
lore:
  mode: contains-all
  lines:
    - {text: "Legendary", mode: starts-with}   # condition A
    - {text: "sword", mode: ends-with}         # condition B
```
On X and Y both conditions are only satisfied by the same line `Legendary sword`, but every
condition needs a different lore line.

X no · Y no · an item with lore `Legendary axe` / `Old sword`: yes

**Any of these lines** (`contains-any`) and **none of these lines** (`none`)

``` yaml
lore:
  mode: contains-any
  lines: ["Rare", "Epic"]
```
X yes · Y yes · Z no · W no

``` yaml
lore:
  mode: none
  lines: [{text: "quest item", mode: contains, ignore-case: true}]
```
No lore line contains `quest item` (in any case) — for example to keep items of other quests out.

X yes · Y yes · Z yes · W no

**Alternative sets**

``` yaml
lore:
  - ["Rare", "Legendary sword", "Soulbound"]    # set A: exactly this lore
  - mode: contains-all                          # set B
    lines:
      - {text: "legendary", mode: contains, ignore-case: true}
      - "Rare"
```
(exactly the lore of X) **or** (a line containing `legendary` and another line `Rare`, any order) —
for example to accept items made before and after a change of their lore.

X yes (A) · Y yes (B) · Z no · W no

## Putting it together

``` yaml
item-requirement:
  - material: [DIAMOND_SWORD, NETHERITE_SWORD]      # definition 1
    enchantments:
      - [SHARPNESS:5, MENDING:0]                    #   set 1a
      - [SMITE:5, MENDING:0]                        #   set 1b
    lore:
      mode: none
      lines: [{text: "quest item", mode: contains, ignore-case: true}]
  - material: TRIDENT                               # definition 2
    enchantments:
      mode: contains-any
      values: [RIPTIDE, CHANNELING]
```

```
( material is a diamond or netherite sword
  AND ( (Sharpness V AND no Mending) OR (Smite V AND no Mending) )
  AND no lore line contains "quest item" )
OR
( material is a trident AND (Riptide OR Channeling) )
```

| Item                                          | Matches | Why                               |
|-----------------------------------------------|---------|-----------------------------------|
| Netherite sword, Sharpness V, Unbreaking III  | yes     | definition 1, set 1a              |
| Diamond sword, Smite V, lore `Rare`           | yes     | definition 1, set 1b              |
| Diamond sword, Sharpness V, Mending           | no      | 1a and 1b both forbid Mending     |
| Diamond sword, Sharpness V, lore `[Quest Item]` | no    | the lore breaks `none`            |
| Trident, Loyalty III                          | no      | definition 2: no Riptide or Channeling |
| Trident, Channeling                           | yes     | definition 2                      |

## defined-exact

`defined-exact` builds one item from the definition and compares it with the full item data, like
`exact-match: true` does for `item`. Therefore every option must describe exactly one value:

* `material`: a single material, not a list and not an item tag,
* `damage`, `custom-model-data`, `item-model`: a single value, not a list,
* `name`: a single matcher with mode `equals` and without `ignore-case` (or mode `absent`),
* `lore`: a single set with mode `exact`, every line with mode `equals` and without `ignore-case`,
* `enchantments`: a single set with mode `contains-all` or `exact`, every enchantment with a single
  level (`SHARPNESS:5`; `SHARPNESS:0` is allowed and adds nothing),
* `item-flags`: a single set with mode `contains-all` or `exact`.

``` yaml
item-requirement:
  type: defined-exact
  material: DIAMOND_SWORD
  name: "&6Excalibur"
  lore: ["&7The sword of kings"]
  enchantments: [SHARPNESS:5, UNBREAKING:3]
  item-flags: HIDE_ENCHANTS
```
