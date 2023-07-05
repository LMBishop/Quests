**Configuration problems** are designed to inform you when you have an **error** or **potential misconfiguration**. They may appear when reloading Quests, for example:

![](https://i.imgur.com/5o7EyVm.png)

These problems are designed to be as readable as possible, allowing self-diagnosis for your configuration. Warnings are also used to spot common misconfigurations, which may lead to quests not working as expected, thus being interpreted as a bug. 

## Types of problem
|Type|Description|
| :---: | --- |
|Error|Errors prevent the specific quest from loading. These can be overidden in the config at `options.error-checking.override-errors`. Examples include an incorrect quest ID or malformed YML file.|
|Warning|Warnings have no impact other than to inform you that a quest may not work as expected. Examples include an invalid material for a blockbuildcertain task, or a required quest which does not exist.|

## Understanding the problems
Problems generally follow this format:
```
<name of file> ----
 | - <type of problem>: <description of problem> :<location of problem>
```

**Example 1**

The following error will show if you try to create a `blockbreakcertain` task without specifying how many blocks or what block you need to break.
```
example1.yml ----
 | - E: Required field 'amount' is missing for task type 'blockbreakcertain' :tasks.miningcertain.amount
 | - E: Required field 'block' is missing for task type 'blockbreakcertain' :tasks.miningcertain.block
```
In the above example, the problem is an Error (as denoted by E) and will prevent the quest (`example1`) from loading. At the end, it shows you exactly where the error comes from in the YML file, which looks like this:
```yaml
tasks:
  miningcertain:
    type: "blockbreakcertain"
```
Where it says the location `:tasks.miningcertain.amount`, each dot deontes a level of indentation. So in this case, it is expecting values at `amount` and `block`, but they are not defined. Below is the fixed version.
```yaml
tasks:
  miningcertain:
    type: "blockbreakcertain"
    amount: 10
    block: DIAMOND_ORE
```
**Example 2**

```
example2.yml ----
 | - E: Expected an integer for 'amount', but got 'hey' instead :tasks.inventory.amount
 | - W: Material 'thisisnotablock' does not exist :tasks.inventory.item
 | - W: Quest requirement 'example' does not exist :options.requires
```
```yaml
tasks:
  inventory:
    type: "inventory"
    amount: hey
    item: thisisnotablock
...
options:
  requires:
    - "example"
  ...
```
In this case, the task is broken since instead of numbers for the amount of items needed, the string "hey" is there instead, causing an error. The source of the error is indicated by the location at the end `tasks.inventory.amount`. 

Also, a warning is shown for the item "thisisnotablock" at `tasks.inventory.item` and for the requirement "example" at `options.requires`. These warnings are informing you that the task may not work as "thisisnotablock" is not a real item, and that the quest "example" which is required to have been completed in order to start this quest does not exist. 

Below is a fixed version:
```yaml
tasks:
  inventory:
    type: "inventory"
    amount: 10
    item: DIAMOND
...
options:
  # requirements section removed
  ...
```