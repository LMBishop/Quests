---
title: New task type
parent: Developer
---

# New task type

{% include incomplete.md %}

The following information is for developers who are interested in integrating Quests into their own plugin.

Quests are a set of tasks which the player must complete in return for a reward. The **task** the player must complete depends on the **task type**, and how it is configured. When quests are loaded from the config, they are registered to each task type the quest contains.

## Purpose of a task type
**The role of a task type is to handle the progress of tasks within quests which registered with your type.**

How a task type handles the progress differs with each one. For example, a `blockbreak` task type may increase the progress whenever a block is broken, or a `position` task type may flag the quest as completed when a player is near a defined location.

How a task type works generally follows this format:
1. Listen for related event
2. Iterate over every quest registered to the task type
3. Check if the player has started the quest
4. Iterate over each task of the quest
5. If the task is incomplete, increment the progress according to criteria
6. Mark the task as complete if over threshold

Quests handles the completion of all tasks within a quest and dispatches rewards. **A task type simply handles the events which increment a single task.**

{: .caution }
> Task Types **MUST** be registered before the actual quests from the config are registered. They can be registered in between Quests inital activation and before the server is fully started. For example: you can register new Task Types inside of the `onEnable()` and add Quests to the `depend` or `softdepend` section of your `plugin.yml` to make sure that Quests is ready to accept your registration.
> 
> Attempting to register a task type after the registration period has closed will throw a `IllegalStateException`. The registration period closes and quests are loaded after every plugin has finished loading (on the first server [tick](https://github.com/LMBishop/Quests/blob/master/bukkit/src/main/java/com/leonardobishop/quests/bukkit/BukkitQuestsPlugin.java#L402)). 

## Writing task types
Your new Task Type must extend the [`TaskType`](https://github.com/LMBishop/Quests/blob/master/common/src/main/java/com/leonardobishop/quests/common/tasktype/TaskType.java) class. **`BukkitTaskType` implements `Listener` and automatically registers as an event listener if used with the `BukkitTaskTypeManager`.** Your subclass must pass the name of the Task Type into the constructor, and optionally the authors name and a brief description of the task. 

Example:
```java
public final class MiningTaskType extends BukkitTaskType {

    public MiningTaskType() {
        super("blockbreak", "LMBishop", "Break a set amount of blocks.");
    }

}
```
The name of the Task Type (`blockbreak` in this case) is what server owners put into their config. 
### Bukkit listeners
You can use Bukkit event listeners like you normally would in your own plugin. The class is already registered as a listener by Quests.

It is recommended that  you set your event priority to `MONITOR` (only if you do not cancel the event) and setting `ignoreCancelled` to true in the `EventHandler` annotation.

```java
@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
```
### Configuration
Quests will load the configuration of each task into a `HashMap` in the [`Task`](https://github.com/LMBishop/Quests/blob/master/common/src/main/java/com/leonardobishop/quests/common/quest/Task.java) class. Server owners define their task configuration in the quest config, for example:
```yaml
tasks:
  mining:
    type: "blockbreakcertain"
    amount: 81
    block: GOLD_ORE
```
Will lead to the following map:
```java
configValues = {type="blockbreakcertain";amount=81;block="GOLD_ORE"}
```
These values can be accessed through `Task#getConfigValue(String)`. You can also specify a default value to be returned if a server owner has not defined a value with `Task#getConfigValue(String, String)`.

### Implementing methods
The [`TaskType`](https://github.com/LMBishop/Quests/blob/master/common/src/main/java/com/leonardobishop/quests/common/tasktype/TaskType.java) class has some methods which can be optionally overridden. 

The `onReady()` method is called when the server has registered all quests to your task type.

The `onStart()` method is called when a player starts a quest containing a task of your task type.

The `onDisable()` method is called when the plugin is disabled, or is reloaded. If the plugin is reloaded, this will unregister all quests from your task type, then register the new quests again.

The `validateConfig(String, HashMap<String, Object>)` method is called when each task is loaded. It is used to detect potential configuration issues and can be used to prevent the quest from loading if, for example, a required config option is missing. The first string is the task root (e.g `tasks.mining` in the first example) and the map is the config to check. The return type is a list of `ConfigProblem`, returning a list containing at least one `ConfigProblem` with an `ERROR` type will prevent the quest from loading.

### Modifying progress
You can modify progress by obtaining an instance of a players `QuestProgressFile`, getting the specific `QuestProgress` and then getting the specific `TaskProgress`. You can see an example of this in the [`MiningTaskType`](https://github.com/LMBishop/Quests/blob/master/bukkit/src/main/java/com/leonardobishop/quests/bukkit/tasktype/type/MiningTaskType.java) (which contains comments through the file).