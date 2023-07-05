---
title: API
parent: Developer
---

# API

{: .highlight }
**This page is under construction.** The information contained here may be inaccurate or incomplete. You can help by [contributing information to the wiki](https://github.com/LMBishop/Quests/wiki/Contributing-to-the-wiki).

Quests provides an API for other people to use. **This API is experimental and may be subject to change.** For usages, it is best to take a look into the plugin itself.

## Bukkit
You can get an instance of Quests as you would with any other plugin:
```java
BukkitQuestsPlugin questsPlugin = (BukkitQuestsPlugin) Bukkit.getPluginManager().getPlugin("Quests");
```
From there, you can access the `QPlayerManager`, `QuestManager` etc. See the javadoc in [this file](https://github.com/LMBishop/Quests/blob/master/common/src/main/java/com/leonardobishop/quests/common/plugin/Quests.java), and in each class for more info on its purpose.

### Events
Quests provides some Bukkit events, you can see them all [here](https://github.com/LMBishop/Quests/tree/master/bukkit/src/main/java/com/leonardobishop/quests/bukkit/api/event).

### Registering task types
Task types **must** be registered during startup, after Quests has initialized its task type manager. You cannot register task types after this period as individual quests are registered to their task types for performance reasons.

```java
@Override
public void onEnable() {
    // ...
    Quests questsPlugin = (Quests) Bukkit.getPluginManager().getPlugin("Quests");
    BukkitTaskTypeManager taskTypeManager = (BukkitTaskTypeManager) questsPlugin.getTaskTypeManager();

    taskTypeManager.registerTaskType(new ...);
}
```