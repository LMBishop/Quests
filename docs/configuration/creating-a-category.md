---
title: Creating a category
parent: Configuration
nav_order: 3
---

# Creating a category
{: .no_toc }

Categories are stored in `categories.yml`. On older versions of Quests
they were stored in the main `config.yml`; quests will read categories
from `config.yml` if a `categories.yml` file is not present.


## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

## Category ID

ID of category, this is the text you should enter when putting quests
inside a category.

## GUI title


*`gui-name`*

The custom GUI title to be shown in this category quest menu

## Display

  
*`display`*

The item that is shown in the GUI.

### Name

  
*`display.name`*

The name of the item.

### Lore

  
*`display.lore`*

The lore (description) of the item.

### Type

  
*`display.type`*

The type (material name/id) of item.

## Permission required

  
*`permission-required`*

Whether permission is needed to open this category, or start quests
within it. This permission will follow this format:
`quests.category.<category>`\`.

## Hidden

  
*`hidden`*

Whether this category is shown in the main quests menu.
