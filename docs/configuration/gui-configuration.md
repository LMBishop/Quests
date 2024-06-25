---
title: GUI configuration
parent: Configuration
nav_order: 6
---

# GUI configuration

*See also [Custom GUI items](custom-gui-items) and [Defining
items](defining-items).*

The **GUI configuration** is defined in the `config.yml`. These define
the static UI elements such as the back button, quest locked display
etc. All options accept the standard ItemStack definition format
described in [defining items](defining-items).

## Back button

  
*`gui.back-button`*

The back button displayed within sub menus.

``` yaml
gui:
  # ...
  back-button:
    enabled: true
    slot: 45 
    name: "&cReturn"
    lore:
    - "&7Return to the categories menu."
    type: "ARROW"
```

## Page previous

  
*`gui.page-prev`*

The previous page button displayed on paiginated menus.

``` yaml
gui:
  # ...
  page-prev:
    enabled: true
    slot: 48 
    name: "&7Previous Page"
    lore:
    - "&7Switch the page to page &c{prevpage}."
    type: "FEATHER"
```

The `{prevpage}` variable represents the page number for the previous
page.

## Page next

  
*`gui.page-next`*

The next page button displayed on paiginated menus.

``` yaml
gui:
  # ...
  page-next:
    enabled: true
    slot: 50 
    name: "&7Next Page"
    lore:
    - "&7Switch the page to page &c{nextpage}."
    type: "FEATHER"
```

The `{nextpage}` variable represents the page number for the next page.

## Page description

  
*`gui.page-next`*

The current page item displayed on paginated menus. The amount of this
item will automatically update on the page number.

``` yaml
gui:
  # ...
  page-desc:
    enabled: true
    slot: 49
    name: "&7Page &c{page}"
    lore:
    - "&7You are currently viewing page &c{page}."
    type: "PAPER"
```

The `{page}` variable represents the page number for the current page.

## Quest locked display

  
*`gui.quest-locked-display`*

The item is used to represent locked quests. A quest is locked if its
[requirements](creating-a-quest#requirements) are not met.

``` yaml
gui:
  # ...
  quest-locked-display:
    name: "&c&lQuest Locked"
    lore:
    - "&7You have not completed the requirements"
    - "&7for this quest (&c{quest}&7)."
    - ""
    - "&7Requires: &c{requirements}"
    - "&7to be completed to unlock."
    type: "RED_STAINED_GLASS_PANE"
```

The `{quest}` variable represents the quest [display
name](creating-a-quest#name), with its formatting stripped.

The `{questcolored}` variable represents the quest [display
name](creating-a-quest#name), with its formatting left as is.

The `{questid}` variable represents the quest ID.

The `{requirements}` variable represents the display names of the quests
needed to unlock this quest. By default, this name is truncated to show
only the first quest, with a number after (e.g. "Example II +4 more").
This behaviour is defined at [Basic options § GUI-truncate
requirements](basic-options#gui-truncate-requirements)

## Quest permission display

  
*`gui.quest-permission-display`*

The item is used to represent quests which the player does not have
permission to start.

``` yaml
gui:
  # ...
  quest-permission-display:
    name: "&6&lNo Permission"
    lore:
    - "&7You do not have permission for this"
    - "&7quest (&6{quest}&7)."
    type: "BROWN_STAINED_GLASS_PANE"
```

The `{quest}` variable represents the quest [display
name](creating-a-quest#name), with its formatting stripped.

The `{questcolored}` variable represents the quest [display
name](creating-a-quest#name), with its formatting left as is.

The `{questid}` variable represents the quest ID.

## Quest cooldown display

  
*`gui.quest-cooldown-display`*

The item is used to represent quests which are repeatable, the player
has completed, but are on cooldown.

``` yaml
gui:
  # ...
  quest-cooldown-display:
    name: "&e&lQuest On Cooldown"
    lore:
    - "&7You have recently completed this quest"
    - "&7(&e{quest}&7) and you must"
    - "&7wait another &e{time} &7to unlock again."
    type: "ORANGE_STAINED_GLASS_PANE"
```

The `{quest}` variable represents the quest [display
name](creating-a-quest#name), with its formatting stripped.

The `{questcolored}` variable represents the quest [display
name](creating-a-quest#name), with its formatting left as is.

The `{questid}` variable represents the quest ID.

The `{time}` variable represents the formatted time remaining until the
cooldown period is over. This can be configured in the messages section.

## Quest completed display

  
*`gui.quest-completed-display`*

The item is used to represent quests which are completed and not
repeatable.

``` yaml
gui:
  # ...
  quest-completed-display:
    name: "&a&lQuest Complete"
    lore:
    - "&7You have completed this quest"
    - "&7(&a{quest}&7) and cannot."
    - "&7repeat it."
    type: "GREEN_STAINED_GLASS_PANE"
```

The `{quest}` variable represents the quest [display
name](creating-a-quest#name), with its formatting stripped.

The `{questcolored}` variable represents the quest [display
name](creating-a-quest#name), with its formatting left as is.

The `{questid}` variable represents the quest ID.

## No started quests

  
*`gui.no-started-quests`*

This is shown as the only item in the quest started menu if the player
has not started any quests.

``` yaml
gui:
  # ...
  no-started-quests:
    name: "&c&lNo Started Quests"
    lore:
     - "&7Go start some!"
    type: "FEATHER"
```

## Quest cancel yes

  
*`gui.quest-cancel-yes`*

Confirmation item in the quest cancel menu.

``` yaml
gui:
  # ...
  quest-cancel-yes:
    name: "&a&lConfirm Cancel"
    lore:
    - "&7Confirm you wish to cancel"
    - "&7this quest and lose all"
    - "&7progress."
    type: "GREEN_STAINED_GLASS_PANE"
```

## Quest cancel no

  
*`gui.quest-cancel-no`*

Cancellation item in the quest cancel menu.

``` yaml
gui:
  # ...
  quest-cancel-no:
    name: "&c&lAbort Cancel"
    lore:
    - "&7Return to the quest menu."
    type: "RED_STAINED_GLASS_PANE"
```

## Quest cancel background

  
*`gui.quest-cancel-background`*

Background item in the quest cancel menu.

``` yaml
gui:
  # ...
  quest-cancel-background:
    type: "GRAY_STAINED_GLASS_PANE"
```
