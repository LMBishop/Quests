---
title: Commands and permissions
nav_order: 4
---
# Commands and permissions
This page lists all commands and permissions for Quests. Commands can
also be viewed in-game by simply running `/quests`.

## Commands

- **/quests \[or /q\]** - opens quest GUI
- **/quests help** - view help screen for quests commands
- **/quests started** - view a menu of started quests
- **/quests random \[category\]** - start a random quest \[in a random
  category\]
- **/quests cancel \<questid/\*\>** - cancel quest by id
- **/quests q/quest \<questid\> \<start\|track\|cancel\>** - start quest
  directly by ID.
- **/quests c/category \<categoryid\>** - open category directly by ID.
- **/quests a/admin** - view help for admins
  - **/quests a/admin opengui** - view help for opengui
    - **/quests a/admin opengui q/quest \<player\>** - forcefully open
      quests GUI for player (bypassing quests.command permission)
    - **/quests a/admin opengui c/category \<player\> \<categoryid\>** -
      forcefully open category by ID for player (bypassing
      quests.command permission)
    - **/quests a/admin opengui started \<player\>** - forcefully open
      the started menu for player (bypassing quests.command permission)
  - **/quests a/admin moddata** - view help for opengui
    - **/quests a/admin moddata fullreset \<player\>** - fully clear a
      players data file
    - **/quests a/admin moddata reset \<player\> \<questid\>** - clear a
      players data for a specifc quest
    - **/quests a/admin moddata start \<player\> \<questid\>** - start a
      quest for a player
    - **/quests a/admin moddata complete \<player\> \<questid\>** -
      complete a quest for a player
    - **/quests a/admin moddata random \<player\> \[category\]** - start
      a random a quest for a player \[in a category\]
    - *These commands modify quest progress for players. Use them
      cautiously. Changes are irreversible.*
  - **/quests a/admin items** - view registered quest items.
    - **/quests a/admin items import \<id\>** - import a held quest
      item.
    - **/quests a/admin items give \<player\> \<id\> \[amount\]** - give
      a player a quest item.
  - **/quests a/admin debug** - view help for the [quest
    debugger](tools/quest-debugger).
    - **/quests a/admin debug player \<player\> - show quests progression
      data of a player.
    - **/quests a/admin debug report** - generate a debug report.
    - **/quests a/admin debug quest \<quest/\*\> \<all/self\>** - enable
      debug messages for a specific quest, or all of them.
  - **/quests a/admin types \[type\]** - view activated task types, and
    information on a specific one.
  - **/quests a/admin info \[quest\]** - view loaded quests, and
    information on a specific one.
  - **/quests a/admin reload** - reload Quests.
  - **/quests a/admin config** - see config problems.
  - **/quests a/admin update** - check for updates.
  - **/quests a/admin wiki** - get a link to the wiki.
  - **/quests a/admin about** - view plugin information.

## Permissions

- `quests.command` - to view the quest menu (/quests)
- `quests.command.category` - to view a specific category (/q category)
- `quests.command.started` - to view quest started menu (/q started)
- `quests.command.quest` - to use /q quest
- `quests.command.start` - to start a quest by command (/q start)
- `quests.command.track` - to cancel a quest by command (q track)
- `quests.command.cancel` - to cancel a quest by command (/q cancel)
- `quests.command.random` - to starting a random quest (/q random)
- `quests.admin` - for admin commands

The following are dependent on specific quests & categories:

- `quests.category.<category>` - permission to start quests within
  `(category)`, if `permission-required` for the category is set to
  `true`
- `quests.quest.<quest id>` - permission to start quest `(quest id)`, if
  `permission-required` for the quest is set to `true`

The following are dependent on your configuration:

- `quests.limit.<limit group>` - permission to start a configured amount
  of quests for members of this limit group
