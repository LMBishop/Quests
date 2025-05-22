---
title: Global configurations
parent: Configuration
nav_order: 4
---

# Global configurations

**Global configurations** are intended to be used in place of
**quest-specific configurations**. This helps reduce repetition across
your configuration as you copy common elements from quest to quest, and
also allows you to quickly propagate edits across quests.

## Global task configuration

A global task configuration will add configuration values to all tasks
of a specified type.

For example (in `config.yml`),

``` yaml
...
global-task-configuration:
  types:
    inventory:
      update-progress: true
...
```

This will add to *all* tasks configurations with `type: inventory`
across *all* quests the following: `update-progress: true`. 

{: .note }
Any errors which arise from global task configurations will appear as
if they are coming from individual quests.

Quest-level configurations will override anything set here. To change
this behaviour, modify the [global task configuration
override](basic-options#global-task-configuration-override).

## Global quest display configuration

A global quest display configuration adds text to the display items of
items in the GUI.

By default, this is already configured:

``` yaml
global-quest-display:
  lore:
    append-not-started:
      - ""
      - "&eLeft Click &7to start this quest."
    append-started:
      - ""
      - "&aYou have started this quest."
      - "&ePress Q &7to track this quest."
      - "&eRight Click &7to cancel this quest."
    append-tracked:
      - ""
      - "&aYou are &etracking &athis quest."
      - "&ePress Q &7to stop tracking this quest."
      - "&eRight Click &7to cancel this quest."
```

<img src="https://i.imgur.com/l0FI5Ma.png" width="450px">

If you do not want this, simply remove the section.

## Global macros

Global macros help you reduce repetition across your configuration 
files by centralizing values in your config.yml. Think of them as your 
own variables/placeholders which you can use in your quest files.

You can define macros in your `config.yml`, under the `global-macros`
section:

    global-macros:
      # ...
      # <name of macro>: <string value of macro>
      top-bar: "&6---&7---&6---"

To use these in your quests, reference it by using
`<$m name-of-macro $m>`. Macro names **cannot have spaces**.

For example, to use the `top-bar` macro in `example-quest.yml`:

``` yaml
tasks:
  # ...
display:
  # ...
  lore-normal:
    - "<$m top-bar $>"
    - "..."
  # ...
```

{: .caution }
Macros are replaced by a pre-processor before a configuration is
parsed. This means they have the ability to cause syntax errors in
ways you do not expect if you are not careful. 