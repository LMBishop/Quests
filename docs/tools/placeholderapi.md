---
title: PlaceholderAPI
parent: Tools
---

# PlaceholderAPI
{: .no_toc }

This plugin integrates with PlaceholderAPI and exposes certain values via
placeholders. This can be used with other plugins.

{: .important }
> **No PAPI eCloud download is necessary - placeholders come with the plugin.**
The eCloud extension called 'Quests' is not for this plugin, do not download it!
> 
> {: .warning }
> > Once again, **do not download the eCloud extension**. There will be errors
> > if you do so.

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}


## Common placeholders

### Quest counters

| Placeholder                | Description                                                                  |
|----------------------------|------------------------------------------------------------------------------|
| `%quests_all%`             | Returns the **number** of quests on the server.                              |
| `%quests_completed%`       | **\*** Returns the **number** of quests the player has completed.            |
| `%quests_completedbefore%` | Returns the **number** of quests the player has completed **at least once**. |
| `%quests_started%`         | Returns the **number** of quests which are active for the player.            |
| `%quests_limit%`           | Returns the **number** of quests the player can have started simultaneously. |
| `%quests_categories%`      | Returns the **number** of categories on the server.                          |

### Quest lists

| Placeholder                     | Description                                                                                           |
|---------------------------------|-------------------------------------------------------------------------------------------------------|
| `%quests_all_list%`             | Returns a **comma-seperated\*\* list** of quest **names** on the server.                              |
| `%quests_completed_list%`       | \* Returns a **comma-seperated\*\* list** of quest **names** the player has completed.                |
| `%quests_completedbefore_list%` | Returns a **comma-seperated\*\* list** of quest **names** the player has completed **at least once**. |
| `%quests_started_list%`         | Returns a **comma-seperated\*\* list** of quest **names** which are active for the player.            |
| `%quests_categories_list%`      | Returns a **comma-seperated\*\* list** of category **names** on the server.                           |

### Quest ID lists

| Placeholder                       | Description                                                                                         |
|-----------------------------------|-----------------------------------------------------------------------------------------------------|
| `%quests_all_listid%`             | Returns a **comma-seperated\*\* list** of quest **IDs** on the server.                              |
| `%quests_completed_listid%`       | \* Returns a **comma-seperated\*\* list** of quest **IDs** the player has completed.                |
| `%quests_completedbefore_listid%` | Returns a **comma-seperated\*\* list** of quest **IDs** the player has completed **at least once**. |
| `%quests_started_listid%`         | Returns a **comma-seperated\*\* list** of quest **IDs** which are active for the player.            |
| `%quests_categories_listid%`      | Returns a **comma-seperated\*\* list** of category **IDs** on the server.                           |

**\*** *this does not include quests which have been repeated and not yet completed*

**\*\*** *the delimiter can be changed by adding it in at the end: for example `%quests_completed_listid_ / %` will return a list of completed quests **seperated by the characters ` / ` (including the spaces around it)***

## Advanced placeholders

### Quest details

| Placeholder                                          | Description                                                                                                                  |
|------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------|
| `%quests_quest:<quest-id>%`                          | Returns the **display name** of the quest **`<quest-id>`**.                                                                  |
| `%quests_quest:<quest-id>_started%`                  | Returns **true/false** on whether or not the quest **`<quest-id>`** is started by the player.                                |
| `%quests_quest:<quest-id>_starteddate%`              | Returns a **date formatted as DD/MM/YYYY\*\*\*, or "Never"** on when the quest **`<quest-id>`** was started by the player.   |
| `%quests_quest:<quest-id>_completed%`                | **\*** Returns **true/false** on whether or not the quest **`<quest-id>`** is completed by the player.                       |
| `%quests_quest:<quest-id>_completedbefore%`          | Returns **true/false** on whether or not the quest **`<quest-id>`** is completed by the player **at least once**.            |
| `%quests_quest:<quest-id>_completiondate%`           | Returns a **date formatted as DD/MM/YYYY\*\*\*, or "Never"** on when the quest **`<quest-id>`** was completed by the player. |
| `%quests_quest:<quest-id>_cooldown%`                 | Returns a **time in seconds** of the cooldown for quest **`<quest-id>`**.                                                    |
| `%quests_quest:<quest-id>_timeleft%`                 | Returns the amount of time remaining to complete the quest **`<quest-id>`**.                                                 | 
| `%quests_quest:<quest-id>_canaccept%`                | Returns **true/false** on whether or not the quest **`<quest-id>`** can be started by the player.                            |
| `%quests_quest:<quest-id>_meetsrequirements%`        | Returns **true/false** on whether or not the player has completed the required quests for the quest **`<quest-id>`**.        |
| `%quests_quest:<quest-id>_task:<task-id>_progress%`  | Returns the **progress** of task **`task-id`** on the quest **`<quest-id>`**.                                                |
| `%quests_quest:<quest-id>_task:<task-id>_completed%` | Returns **true/false** on whether or not the task **`task-id`** on the quest **`<quest-id>`** is completed by the player.    |
| `%quests_quest:<quest-id>_task:<task-id>_goal%`      | Returns the **goal** of task **`task-id`** on the quest **`<quest-id>`**.                                                    |
| `%quests_quest:<quest-id>_p:<placeholder>%`          | Returns the **local quest placeholder `<placeholder>`** for the quest **`<quest-id>`**.                                      |

| Placeholder                                 | Description                                                                                                                     |
|---------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| `%quests_tracked%`                          | Returns the **display name** of the **players tracked quest** (or "No tracked quest").                                          |
| `%quests_tracked_started%`                  | Returns **true/false** on whether or not the **players tracked quest** is started.                                              |
| `%quests_tracked_starteddate%`              | Returns a **date formatted as DD/MM/YYYY\*\*\*, or "Never"** on when the **players tracked quest** was started by the player.   |
| `%quests_tracked_completed%`                | **\*** Returns **true/false** on whether or not the **players tracked quest** is completed.                                     |
| `%quests_tracked_completedbefore%`          | Returns **true/false** on whether or not the **players tracked quest** is completed **at least once**.                          |
| `%quests_tracked_completiondate%`           | Returns a **date formatted as DD/MM/YYYY\*\*\*, or "Never"** on when the **players tracked quest** was completed by the player. |
| `%quests_tracked_cooldown%`                 | Returns a **time in seconds** of the cooldown for the **players tracked quest**.                                                |
| `%quests_tracked_timeleft%`                 | Returns the amount of time remaining to complete the **players tracked quest**.                                                 | 
| `%quests_tracked_canaccept%`                | Returns **true/false** on whether or not the **players tracked quest** can be started by the player.                            |
| `%quests_tracked_meetsrequirements%`        | Returns **true/false** on whether or not the player has completed the required quests for the **players tracked quest**.        |
| `%quests_tracked_task:<task-id>_progress%`  | Returns the **progress** of task **`task-id`** on the **players tracked quest**.                                                |
| `%quests_tracked_task:<task-id>_completed%` | Returns **true/false** on whether or not the task **`task-id`** on the **players tracked quest** is completed by the player.    |
| `%quests_tracked_p:<placeholder>%`          | Returns the **local quest placeholder `<placeholder>`** for the **players tracked quest**.                                      |

### Per-category quest counters

| Placeholder                                       | Description                                                                                                      |
|---------------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| `%quests_category:<category-id>%`                 | Returns the **id** of the category **`<category-id>`** (useful to check if it is a valid reference).             |
| `%quests_category:<category-id>_all%`             | Returns the **number** of quests in the category **`<category-id>`**.                                            |
| `%quests_category:<category-id>_completed%`       | **\*** Returns the **number** of quests in the category **`<category-id>`** the player has completed.            |
| `%quests_category:<category-id>_completedbefore%` | Returns the **number** of quests in the category **`<category-id>`** the player has completed **at least once**. |
| `%quests_category:<category-id>_started%`         | Returns the **number** of quests in the category **`<category-id>`** which are active for the player.            |

### Per-category quest lists

| Placeholder                                            | Description                                                                                                                               |
|--------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| `%quests_category:<category-id>_all_list%`             | Returns a **comma-seperated\*\* list** of quest **names** on the server.                                                                  |
| `%quests_category:<category-id>_completed_list%`       | \* Returns a **comma-seperated\*\* list** of quest **names** in the category **`<category-id>`** the player has completed.                |
| `%quests_category:<category-id>_completedbefore_list%` | Returns a **comma-seperated\*\* list** of quest **names** in the category **`<category-id>`** the player has completed **at least once**. |
| `%quests_category:<category-id>_started_list%`         | Returns a **comma-seperated\*\* list** of quest **names** in the category **`<category-id>`** which are active for the player.            |

### Per-category quest ID lists

| Placeholder                                              | Description                                                                                                                             |
|----------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| `%quests_category:<category-id>_all_listid%`             | Returns a **comma-seperated\*\* list** of quest **IDs** on the server.                                                                  |
| `%quests_category:<category-id>_completed_listid%`       | \* Returns a **comma-seperated\*\* list** of quest **IDs** in the category **`<category-id>`** the player has completed.                |
| `%quests_category:<category-id>_completedbefore_listid%` | Returns a **comma-seperated\*\* list** of quest **IDs** in the category **`<category-id>`** the player has completed **at least once**. |
| `%quests_category:<category-id>_started_listid%`         | Returns a **comma-seperated\*\* list** of quest **IDs** in the category **`<category-id>`** which are active for the player.            |

**\*** *this does not include quests which have been repeated and not yet completed*

**\*\*** *the delimiter can be changed by adding it in at the end: for example `%quests_category:<category-id>_completed_listid_ / %` will return a list of completed quests **seperated by the characters ` / ` (including the spaces around it)***

**\*\*\*** *the date format may be adjusted by including it at the end: for example `%quests_q:<quest-name>_completiondate_dd/MM HH:mm:ss%` will return a the date of compltion **formatted as `dd/MM HH:mm:ss`** - you may use any letter listed [here](https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html)*

## Caching placeholders

Placeholders may be cached for a short period (10 seconds by default) to help improve performance. To do this, **simply add `_cache` to the end of any placeholder**. Note: date formats are automatically cached as parsing them may be heavy.

**Examples:**
```
%quests_all_cache%
%quests_completedbefore_listid_cache%
%quests_quest:<quest-id>_task:<task-id>_progress_cache%
```

The length of time Quests retains this cache can be configured in the configuration under `options.placeholder-cache-time`. By default, this cache is **10 seconds**.

```yaml
options:
  ...
  placeholder-cache-time: 10
```