#
# Documentation
# -------------
#
# The parameters a quest can have is documented on the wiki.
# https://quests.leonardobishop.com/configuration/creating-a-quest.html
#
#
# How to create a quest
# ---------------------
#
# Each file ín the 'quests' folder defines a single quest.
# The name of the file is the quest ID. These must be alphanumeric and unique.
# Quest files must be in the .yml format.
#
# A quest is a series of tasks which players must complete for a reward and may require a
# previous quest to start.
# A task is an objective such as breaking blocks or obtaining items.
# A reward is a command executed by the SERVER. Use {player} to get the players name.
#
# Most task types will allow you to restrict them to a certain world. Check the wiki for which ones.
#
# A quest can have a 'startstring' (optional).
#  - This will be sent to the player when they start the quest.
# A quest can have a 'rewardstring' (optional).
#  - This will be sent to the player when they complete the quest.
# An example of the startstring/rewardstring in use can be seen in the quest example4.
#
# Each quest will have ONE "display" item, this is the item shown to the player in the GUI.
# The display item will have a "name", a "type" and TWO lores (descriptions).
# The name is the name of the item, the type is the material and the lore is the text underneath the item (when mouse-over-ing).
# The first lore you must give is called 'lore-normal'. This is the lore seen if the player has not started the quest.
# The second lore you must give is 'lore-started'. This will be APPENDED to the first lore IF the player has started the quest - useful for putting progression.
# Within the lores you can get the players progress for each task. Use {TASKID:progress} (replace TASKID with the ID of the task).
# You can also get if a task is complete. Use {TASKID:complete} (replace TASKID with the ID of the task).
#
# Quests can be put inside a category. When a player runs /quests they will first see a menu of categories.
# They can click one and another menu of quests under that category will show up.
#
#
# Task types
# ----------
#
# You can see other task types here:
# https://quests.leonardobishop.com/task-types/
#
#
# Need help?
# ----------
#
# Find the Discord link in the README.md, or open an issue on GitHub
# https://github.com/LMBishop/Quests/issues
# https://github.com/LMBishop/Quests/blob/master/README.md
#