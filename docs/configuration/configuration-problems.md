---
title: Configuration problems
parent: Configuration
nav_order: 9
---

# Configuration problems

If you have a configuration error, Quests will log details both to the console and in-game (if you use `/q a reload`).

![](https://i.imgur.com/5o7EyVm.png)

These problems are designed to be as readable as possible, allowing self-diagnosis for your configuration. Warnings are also used to spot common misconfigurations, which may lead to quests not working as expected, thus being interpreted as a bug. 

Most problems have extended descriptions if you mouse-over them in-game.

## Types of problem

|  Type   | Description                                                                                                                                                                                          |
|:-------:|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  Error  | Errors prevent the specific quest from loading. These can be overidden in the config at `options.error-checking.override-errors`. Examples include an incorrect quest ID or malformed YAML file.     |
| Warning | Warnings have no impact other than to inform you that a quest may not work as expected. Examples include an invalid material for a blockbuildcertain task, or a required quest which does not exist. |

## Understanding a problem
Problems generally follow this format:
```
<name of file> ----
 | - <type of problem>: <description of problem> :<location of problem>
```

**Example 1**

Take the following configuration.

```yaml
tasks:
  damage:
    type: "dealdamage"
```
The following error will show if you try to create a `dealdamage` task without specifying how much damage needs to be dealt.
```
example1.yml ----
 | - E: Required field 'amount' is missing for task type 'dealdamage' :tasks.damage.amount
```
In the above example, the problem is an error (as denoted by E) and will prevent the quest (`example1`) from loading. 
The descriptor at the end shows you exactly where the error comes from in the YML file, which looks like this:

The source of the error is given by `:tasks.damage.amount`, where each dot denotes a level of indentation. In this case, it is expecting a value at `amount`, but it is not defined. 

Below is the fixed version.

```yaml
tasks:
  damage:
    type: "dealdamage"
    amount: 10
```

**Example 2**

```
example2.yml ----
 | - E: Expected an integer for 'amount', but got 'ten' instead :tasks.inventory.amount
 | - W: Material 'notablock' does not exist :tasks.inventory.item
 | - W: Quest requirement 'example' does not exist :options.requires
```
```yaml
tasks:
  inventory:
    type: "inventory"
    amount: ten
    item: notablock
...
options:
  requires:
    - "example"
  ...
```
In this case, the task is broken since instead of numbers for the amount of items needed (`amount`), the string "ten" is there instead, causing an error. The source of the error is indicated by the location at the end `tasks.inventory.amount`. 

Also, a warning is shown for the item "thisisnotablock" at `tasks.inventory.item` and for the requirement "example" at `options.requires`. These warnings are informing you that the task may not work as expected, as "thisisnotablock" is not a real item, and that the quest "example" which is required to have been completed in order to start this quest does not exist. 

Unlike errors, warnings do not prevent quests from being loaded.

Below is a fixed version:
```yaml
tasks:
  inventory:
    type: "inventory"
    amount: 10
    item: DIAMOND
# ...
options:
  # requirements section removed
  # ...
```