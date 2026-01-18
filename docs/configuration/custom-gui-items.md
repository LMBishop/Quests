---
title: Custom GUI items
parent: Configuration
nav_order: 7
---
# Custom GUI items

**Custom GUI items** are dummy items added to category, quest and
started menus. This can be used to help stylise your quests GUI.

This can be done in the `config.yml`:

``` yaml
custom-elements:
  "categories": # apply to the categories menu (the main menu by default)
    0:   # <--- slot 1, note the slots start from 0! so 0 = slot 1 in this case
      display:
        name: "&cExample Custom Item (slot 1)"
        lore:
          - "&7This is a custom item which can be added"
          - "&7to your menus. This is purely cosmetic."
          - ""
          - "&7Two empty slots should follow."
        type: "DIAMOND_BLOCK"
      result: "CLOSE_MENU" # by default DO_NOTHING, can be also REFRESH_PANE
    1:   # <--- start from slot 2
      spacer: true # empty slot in GUI
      repeat: 2 # repeats for 2 slots
    3:   # <--- start from slot 4
      display:
        name: "&cExample Custom Item (slots 4 - 7)"
        lore:
          - "&7This is a custom item which can be added"
          - "&7to your menus, but in slot 4 and repeated"
          - "&73 times."
          - "&7"
          - "&7This will come after 2 empty slots."
          - "&7"
          - "&7This is purely cosmetic."
        type: "NETHERRACK"
      repeat: 3 # repeats for 3 more slots
      commands:
          - "this command will be executed if the player click on this item"
```

The optional `repeat` field will repeat the item for consecutive slots
after that.

The optional `commands` list will execute commands if the player clicks
on the item. The `{player}` placeholder can be used to substitute the
player name.

These custom elements take precedence over quest items, as quests and
categories will fill empty slots once all the custom items have been
set.

![Example of how custom GUI items are laid out](https://i.imgur.com/5odcqM9.png)

To add a custom item within the quest menu itself, you must specify the
category, or if categories are disabled you can specify "quests"
instead:

``` yaml
custom-elements:
  "c:<category-name>": # apply to <category-name> menu
    # ...
  "quests": # apply to whole quests menu if categories are disabled
    # ...
```

Additionally, you can add custom items to the started quests menu by
specifying it with "started":

``` yaml
custom-elements:
  "started": # apply to the started quests menu
    # ...
```