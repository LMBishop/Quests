---
title: Quest debugger
parent: Tools
---

# Quest debugger

The **quests debugger** allows you to see why a quest may not be working
as intended. When turned on for a quest, it will print out what a task
type is doing and how it is evaluating it. This can be helpful to see
why a specific quest is not accepting a specific action.

## Using the debugger

The debugger can be enabled with **/q a debug quest \<quest/\*\>
\<all/self\>**. Using \* in place of \<quest\> will enable it for all
quests. Enabling it for all will show debug logs for every player,
whereas self will show it for just yourself.

<img src="https://i.imgur.com/Sb5DrpJ.png" height=50>

## Example

We may want to debug the following task:

``` yaml
tasks:
  mining:
    type: "blockbreakcertain"
    amount: 30
    blocks:
    - DARK_OAK_LOG
    - DARK_OAK_PLANKS
    reverse-if-placed: false
```

When breaking `PACKED_ICE`, the debugger sends this output:

<img src="https://i.imgur.com/2GKba8i.png">

Here it is telling us that the task type is checking the broken block
against all the blocks in the `blocks` list, and not finding a match,
thus skipping this task.

Now, when breaking `DARK_OAK_PLANKS`:

<img src="https://i.imgur.com/2nI8uCH.png">

The debugger tells us that it finds a match and increments the task
progress.

This can be useful when trying to work out why a task may not be
working, such as in the case where you think you're breaking a block of
a specific type, but in reality it has a different type.
