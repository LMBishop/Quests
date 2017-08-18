# Quests
Spigot Plugin // This plugin will allow your players to do quests in-game to earn rewards. This can all be configured.

## Making your own Quest Type
With Quests 1.10 you can now make your own Quest Type. There are two ways of registering a Quest with a custom Quest Type: in the config and through code.

This guide will show you how to create a 'WALKING' Quest.

### Part one
To create a Quest with a custom Quest Type, first go to the config and make a new Quest as you normally would do but set the 'type' to 'CUSTOM'. Then add a new key called 'custom-type' and set that to 'WALKING' (or whatever you want the quest type to be)
```yaml
'walking1':
  type: CUSTOM
  custom-type: 'WALKING'
  value: '100'
  redoable: false
  cooldown:
    enabled: true
    minutes: 30
  display:
    item: 'GRASS'
    name: '&9Walking I'
    lore:
     - '&7Walk 100 blocks.'
     - ''
     - '&7Rewards:'
     - '&7$500'
     - ''
     - '&7Progress: %progress% blocks'
  rewards:
   - 'type:command, value:[eco give %player% 500]'
  rewardstring:
   - '&a$500 added to your in-game balance.'
```

That's part one done.
### Part two
Now in your plugin you need to handle the Quest. For our example, we will create a new Listener and an EventHandler.
```java
public class EventPlayerJoin implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerMoveEvent event) {}
}
```

Inside the event handler we need to make sure Quests is running on the server.
```java
if (Bukkit.getPluginManager().isPluginEnabled("Quests")) {}
```

Now, inside that we are going to make sure the player has moved one whole block.
```java
public class EventPlayerJoin implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerMoveEvent event) {
    if (Bukkit.getPluginManager().isPluginEnabled("Quests")) {
      if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
        // The player has moved a block! Now we need to add progress to the correct quests.
      }
    }
  }
}
```

That's part two done. 
### Part three
Now we need get the 'CUSTOM WALKING' Quests. To do this, we need to get the ``QuestManager``.
```java
QuestManager questManager = Quests.getInstance().getQuestManager();
```
The ``QuestManager`` stores all the Quests. Using the ``QuestManager``, we can get a list of blacklisted worlds. These are worlds which Quests cannot be accessed from and we dont want the player to be able to progress in blacklisted worlds. These worlds are listed in 'blacklisted-worlds' in the config. To get these worlds, do the following:
```java
if (questManager.getBlacklistedWorlds().contains(event.getPlayer().getWorld().getName())) {
  return;
}
```
Now that we have that sorted, we need to get the ``QuestData`` class. This class contains methods related to the data file.
```java
QuestData questData = Quests.getInstance().getQuestData();
```
This class will see some usage later. Now we need to get all the 'CUSTOM WALKING' Quests and loop through them. To do this, we will use the ``QuestManager``.
```java
for (Quest quest : questManager.getQuestsByCustomType("WALKING")) {
  // Stuff
}
```
Using the ``QuestManager``, you can get any Quest, but we only want the quests that we want to add progress to. The method ``getQuestsByCustomType(String)`` will get all Quests which have a 'custom-type' of 'WALKING'.

Now we want to check if the player has started that quest and quest-specific settings, such as restricted worlds. To do this we need both the ``QuestData`` class and ``QuestManager`` class. What's the difference between blacklisted and restricted worlds? Blacklisted worlds are where Quests cannot be used at all, restricted worlds are Quest-specific and are worlds where Quests can only progress in. 
```java
// Check if player has started the quest, if not skip
if (!questData.getStartedQuests(event.getPlayer().getUniqueId()).contains(quest.getNameId())) {
  continue;
}
					
// The check if the quest is world-restricted
if (quest.isWorldsRestriced()) {
  if (!quest.getAllowedWorlds().contains(event.getPlayer().getWorld().getName())) {
    continue;
  }
}
```
Now that we have check if it's world restricted we can finally add Quest progress to the player. This can be done in one line.
```java
questData.addProgress(quest, event.getPlayer().getUniqueId());
```

Finally, to finish this off we need to check if the player has completed the Quest.
```java
if (questData.getProgress(quest, event.getPlayer().getUniqueId()) >= Integer.parseInt(quest.getCompletionValue())) {
  questData.completeQuest(quest, event.getPlayer().getUniqueId());
}
```
That's it!
```java
@EventHandler
	public void onPlayerJoin(PlayerMoveEvent event) {
		if (Bukkit.getPluginManager().isPluginEnabled("Quests")) {
			if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
				QuestManager questManager = Quests.getInstance().getQuestManager();

				if (questManager.getBlacklistedWorlds().contains(event.getPlayer().getWorld().getName())) {
					return;
				}

				QuestData questData = Quests.getInstance().getQuestData();

				for (Quest quest : questManager.getQuestsByCustomType("WALKING")) {

					if (!questData.getStartedQuests(event.getPlayer().getUniqueId()).contains(quest.getNameId())) {
						continue;
					}
					
					if (quest.isWorldsRestriced()) {
						if (!quest.getAllowedWorlds().contains(event.getPlayer().getWorld().getName())) {
							continue;
						}
					}
					
					questData.addProgress(quest, event.getPlayer().getUniqueId());

					if (questData.getProgress(quest, event.getPlayer().getUniqueId()) >= Integer.parseInt(quest.getCompletionValue())) {
						questData.completeQuest(quest, event.getPlayer().getUniqueId());
					}
				}
			}
		}
  }
}	 
```
