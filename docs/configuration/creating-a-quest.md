---
title: Creating a quest
parent: Configuration
nav_order: 2
---

# Creating a quest
{: .no_toc }

Each file inside the `quests` subfolder represents a
single quest. The **name** of the file represents the **quest id** and
must be alphanumeric (excluding the .yml extension).

{: .note }
An example can be seen in the [default
configuration](https://github.com/LMBishop/Quests/blob/master/bukkit/src/main/resources/resources/bukkit/quests/example1.yml).

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

## Quest ID

The quest ID is the name of the file (excluding .yml extension) and must
alphanumeric and unique. This ID is used as the reference in commands,
players' quest progress file and in placeholders.

## Tasks

  
*`tasks`*

Tasks are the objectives the player must do to complete the quest.
Simalar to quest IDs, there are task IDs. They can be identical to the
quest ID but must be unique to each other.

For help on adding the tasks, refer to [task configuration
layout](../task-types)

## Display

  
*`display`*

This is the item which will be shown to the player in the quest GUI.

### Name

  
*`display.name`*

The name of the item. This is also the name used in chat messages.

``` yaml
display:
  name: "&cExample I (Single Task)"
```

### Normal lore

  
*`display.lore-normal`*

The lore (description) of the item as seen if the quest is not started.

``` yaml
display:
  ...
  lore-normal:
    - "&cThis category is designed to show you the different"
    - "&cattributes a quest can have."
    - ""
    - "&7This quest requires you to:"
    - "&7 - Break &f30 blocks&7."
    - ""
    - "&7Rewards:"
    - "&7 - &f10 &7diamonds."
```

### Started lore

  
*`display.lore-started`*

The lore (description) of the item **appended to `lore-normal`** if the
quest is started. This is a good place to put progression details. To
get the progression of a player in a task, write `{TASKID:progress}` and
replace `TASKID` with the ID of the task you want to get the progress
for. Alternatively, you can write `{TASKID:complete}` to get if the task
is complete. There is also `{TASKID:goal}` returning the progress to be
reached.

``` yaml
display:
  # ...
  lore-started:
    - ""
    - "&7Your current progression:"
    - "&7 - &f{mining:progress}&7/30 blocks broken."
```

### Type

  
*`display.type`*

The type (material name) of item.

``` yaml
display:
  # ...
  type: "WOODEN_PICKAXE"
```

## Rewards

  
*`rewards`*

**Optional.** This is a list of commands which will be executed when the
player completes the quest. You can use `{player}` and the players name
will be substituted in place. Commands starting with `player: ` will be
dispatched by the player.

``` yaml
rewards:
  - "give {player} diamond 10"
```

## Start commands

  
*`startcommands`*

**Optional.** This is a list of commands which will be executed when the
player starts the quest. You can use `{player}` and the player's name
will be substituted in place. Commands starting with `player: ` will be
dispatched by the player.

``` yaml
startcommands:
  - "broadcast {player} has started a quest"
```

## Cancel commands


*`cancelcommands`*

**Optional.** This is a list of commands which will be executed when the
player cancels the quest. You can use `{player}` and the player's name
will be substituted in place. Commands starting with `player: ` will be
dispatched by the player.

``` yaml
cancelcommands:
  - "broadcast {player} has cancelled a quest"
```

## Expiry commands


*`expirycommands`*

**Optional.** This is a list of commands which will be executed when the
the player's quest expires. You can use `{player}` and the player's name
will be substituted in place. Commands starting with `player: ` will be
dispatched by the player.

``` yaml
expirycommands:
  - "broadcast {player}'s quest has expired"
```

## Start string

  
*`startstring`*

**Optional.** This is a list of messages which will be sent to the
player when they start the quest. This is useful for telling the player
their objectives.

``` yaml
startstring:
 - " &8- &7You must break 30 blocks."
```

## Cancel string


*`cancelstring`*

**Optional.** This is a list of messages which will be sent to the
player when they cancel the quest.

``` yaml
cancelstring:
 - " &8- &7You cancelled the quest to break 30 blocks."
```

## Expiry string


*`expirystring`*

**Optional.** This is a list of messages which will be sent to the
player when their quest has expired.

``` yaml
expirystring:
 - " &8- &7The quest to break 30 blocks just expired."
```

## Reward string

  
*`rewardstring`*

**Optional.** This is a list of messages which will be sent to the
player when they complete the quest. This is useful for telling the
player their rewards.

``` yaml
rewardstring:
 - " &8- &7You have received 10 dimaonds."
```

## Vault reward


*`vaultreward`*

**Optional.** The Vault reward is an amount of Vault economy money
that will be given to the player when they complete the quest.

``` yaml
vaultreward: 600.0
```

## Placeholders

  
*`placeholders`*

**Optional.** This is a set of placeholders which can be accessed using
PlaceholderAPI. To get the progression of a player in a task, write
`{TASKID:progress}` and replace `TASKID` with the ID of the task you
want to get the progress for. Alternatively, you can write
`{TASKID:complete}` to get if the task is complete. There is also
`{TASKID:goal}` returning the progress to be reached.

``` yaml
placeholders:
  description: "&7Break &f30 blocks &7of any type."
  progress: " &8- &f{mining:progress}&7/30 broken"
```

These placeholders will be called using PlaceholderAPI. See [quest
progress in scoreboard](../guides/quest-progress-in-scoreboard) for a
guide which utilises this feature.

## Progress placeholders

*`progress-placeholders`*

**Optional.** This is a list of placeholders which represent the progress
of each task. These are used by the [bossbar](/configuration/basic-options#bossbar)
and [actionbar](/configuration/basic-options#actionbar) configuration options.

You can define a placeholder for each task, or for all of them as a catch-all.

```yaml
progress-placeholders:
  <task-name>: "Progress for <task-name>"
  '*': "Progress for tasks not defined above"
```

For example, in an actual quest:

```yaml
tasks:
  mining:
    type: "blockbreak"
    amount: 100
  building:
    type: "blockplace"
    amount: 100
# ... 
progress-placeholders:
  mining: "&f{mining:progress}/100 &7blocks broken"
  building: "&f{building:progress}/100 &7blocks placed"
```
## Options

  
*`options`*

This section defines quest-specific options.

### Category

  
*`options.category`*

**Optional.** The category the quest will be in. You should put the ID
of the category here.

``` yaml
options:
  # ...
  category: "example"
```

### Requirements

  
*`options.requires`*

**Optional.** List of Quest IDs the player must complete before being
able to start this quest.

``` yaml
options:
  # ...
  requires:
   - "quest-id"
```

### Permission required

  
*`options.permission-required`*

**Optional.** Whether or not the quest should require a permission to
start. The permission will be `quests.quest.<id>`.

``` yaml
options:
  # ...
  permission-required: false
```

### Cancellable

  
*`options.cancellable`*

**Optional.** Whether or not this quest can be cancelled. If global or
local quest autostart is enabled, or is cancelling quests is disabled,
then this option is ignored.

``` yaml
options:
  # ...
  cancellable: false
```

### Counts towards limit

  
*`options.counts-towards-limit`*

**Optional.** Whether or not this quest counts towards the players quest
started limit. If global quest autostart is enabled, this will have no
effect as quest limits are disabled.

``` yaml
options:
  # ...
  counts-towards-limit: false
```

### Counts towards completed


*`options.counts-towards-completed`*

**Optional.** Whether or not this quest counts towards the players quests
completed. The option is currently used only for the plugin placeholders.

``` yaml
options:
  # ...
  counts-towards-completed: false
```

### Hidden


*`options.hidden`*

**Optional.** Whether or not this quest should be hidden from the plugin menus.

``` yaml
options:
  # ...
  hidden: true
```

### Repeatable

  
*`options.repeatable`*

**Optional.** Whether or not the quest can be replayed.

``` yaml
options:
  # ...
  repeatable: false
```

### Cooldown

  
*`options.cooldown`*

**Optional.** Whether ot not the quest is placed on cooldown or is
immediately replayable.

``` yaml
options:
  # ...
  cooldown:
    enabled: true
    time: 1440     # minutes
```

### Time limit

  
*`options.time-limit`*

**Optional.** Whether or not this quest has a time limit to complete it.
If the time limit is reached, the quest will be cancelled and progress
reset.

``` yaml
options:
  # ...
  time-limit:
    enabled: true
    time: 1440     # minutes
```

### Sort order

  
*`options.sort-order`*

**Optional.** How the plugin sorts the quests in the GUI, lower numbers
come first.

``` yaml
options:
  # ...
  sort-order: 1
```

### Autostart

  
*`options.autostart`*

**Optional.** Whether or not the quest should automatically be started.
This is similar to enabling quest autostart for the entire plugin, but
specific only to this quest, meaning it cannot be cancelled and counts
towards the players quest started limit.

See [§ counts towards
limit](#counts-towards-limit) if you do not
want autostart quests to count towards the quest started limit.

``` yaml
options:
  # ...
  autostart: true
```

### Completed display

  
*`options.completed-display`*

**Optional.** The display item this quest should take if it is
completed. This accepts the standard ItemStack definition format
described in [defining items](defining-items.md). If this option
is not specified, the display item [defined in the main
config.yml](gui-configuration#quest-completed-display) will
be used.

``` yaml
options:
  # ...
  completed-display:
    type: "STEAK"
```

### Cooldown display

  
*`options.cooldown-display`*

**Optional.** The display item this quest should take if it is on
cooldown. This accepts the standard ItemStack definition format
described in [defining items](defining-items). If this option
is not specified, the display item [defined in the main
config.yml](gui-configuration#quest-cooldown-display) will be
used.

``` yaml
options:
  # ...
  cooldown-display:
    type: "STEAK"
```

### Permission display

  
*`options.permission-display`*

**Optional.** The display item this quest should take if the player does
not have permission to start it. This accepts the standard ItemStack
definition format described in [defining
items](defining-items). If this option is not specified, the
display item [defined in the main
config.yml](gui-configuration#quest-permission-display) will
be used.

``` yaml
options:
  # ...
  permission-display:
    type: "STEAK"
```

### Locked display

  
*`options.locked-display`*

**Optional.** The display item this quest should take if the player has
not unlocked it. This accepts the standard ItemStack definition format
described in [defining items](defining-items). If this option
is not specified, the display item [defined in the main
config.yml](gui-configuration#quest-locked-display) will be
used.

``` yaml
options:
  # ...
  locked-display:
    type: "STEAK"
```
