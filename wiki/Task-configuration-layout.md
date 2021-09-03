This shows you how to lay out the [task types](https://github.com/LMBishop/Quests/wiki/Task-Types) in the config. This should be under the `tasks` section in the quest. _Make sure indentation is correct with the YAML file!_

**Note:** This list will not contain task types which were written by someone else. [Click here](https://github.com/LMBishop/Quests/wiki/Task-Types) to see the task types which come with the plugin.
## `blockplace`
Place a set amount of blocks.
```yaml
building:
  type: "blockplace"
  amount: 10                            # amount of blocks to be placed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `blockplacecertain`
Place a set amount of a specific block.
```yaml
buildingstone:
  type: "blockplacecertain"
  amount: 10                            # amount of blocks to be placed
  block: STONE                          # name of block (minecraft name)
  data: 1                               # (OPTIONAL) data code
  reverse-if-broken: false              # (OPTIONAL) if true, blocks of same type broken will reverse progression (prevents silk-touch exploit)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
The `data` field may not work on newer Spigot versions. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for item names. 

The `reverse-if-broken` field will subtract from the progress if the block of the same type is broken, rather than placed, which prevents players from breaking then placing the blocks.

**Alternative layout**

The alternative layout allows multiple blocks to be specified, which all count towards the progress.
```yaml
buildingmultiple:
  type: "blockplacecertain"
  amount: 10                            # amount of blocks to be placed
  blocks:                               # name of blocks which will count towards progress
   - STONE
   - GOLD_ORE                           
  reverse-if-broken: false              # (OPTIONAL) if true, blocks of same type broken will reverse progression (prevents silk-touch exploit)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `blockbreak`
Break a set amount of blocks.
```yaml
mining:
  type: "blockbreak"
  amount: 10                            # amount of blocks to be broken
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `blockbreakcertain`
Break a set amount of a specific block.
```yaml
miningstone:
  type: "blockbreakcertain"
  amount: 10                            # amount of blocks to be brkoen
  block: STONE                          # name of block (can be id or minecraft name)
  data: 1                               # (OPTIONAL) data code 
  reverse-if-placed: false              # (OPTIONAL) if true, blocks of same type placed will reverse progression (prevents silk-touch exploit)
  check-coreprotect: false              # (OPTIONAL) if true and CoreProtect is present, the plugin will check its logs for player placed blocks
  check-coreprotect-time: 3600          # (OPTIONAL) time in seconds for the maximum logging period to check
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
The `data` field may not work on newer Spigot versions. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for item names. 

The `reverse-if-broken` field will subtract from the progress if the block of the same type is broken, rather than placed, which prevents players from breaking then placing the blocks.

Using `check-coreprotect` requires the CoreProtect plugin installed, and requires `check-coreprotect-time` to also be specified. Quests will query CoreProtect when a block is broken to see if any other player has placed a block there. There is a short delay when a player places a block and when it is recorded in CoreProtect, so this may not function as expected in creative mode, where block breaking is instant.

**Alternative layout**

The alternative layout allows multiple blocks to be specified, which all count towards the progress.
```yaml
miningmultiple:
  type: "blockbreakcertain"
  amount: 10                            # amount of blocks to be placed
  blocks:                               # name of blocks which will count towards progress
   - STONE
   - GOLD_ORE                           
  reverse-if-broken: false              # (OPTIONAL) if true, blocks of same type broken will reverse progression (prevents silk-touch exploit)
  check-coreprotect: false              # (OPTIONAL) if true and CoreProtect is present, the plugin will check its logs for player placed blocks
  check-coreprotect-time: 3600          # (OPTIONAL) time in seconds for the maximum logging period to check
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `mobkilling`
Kill a set amount of entities.
```yaml
mobkilling:
  type: "mobkilling"
  amount: 10                            # amount of mobs to be killed
  hostile: true                         # (OPTIONAL) whether or not the mob is hostile - default: both
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Not specifying the `hostile` field will result in both hostile and non-hostile mobs counting towards progress.
## `mobkillingcertain`
Kill a set amount of a specific entity type.
```yaml
mobkillingblaze:
  type: "mobkillingcertain"
  amount: 10                            # amount of mobs to be killed
  mob: BLAZE                            # name of mob
  name: &cInferno                       # (OPTIONAL) this only allows blazes called "&cInferno" - default: any name
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Ensure any colour codes in the `name` of the entity are specified, otherwise the match will not work. For a list of entities, [click here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html).

**Alternative layout**

The alternative layout allows multiple names to be specified, which all count towards the progress.
```yaml
mobkillingblazemultiple:
  type: "mobkillingcertain"
  amount: 10                            # amount of mobs to be killed
  mob: BLAZE                            # name of mob
  names:                                # (OPTIONAL) this only allows blazes called "&cInferno" OR "&6Furnace" - default: any name
   - "&cInferno"
   - "&6Furnace"
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `dealdamage`
Deal a certain amount of damage.
```yaml
dealdamage:
  type: "dealdamage"
  amount: 100                           # amount of damage inflicted (HP)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Damage is measured in HP, 1 heart = 2 HP. A player has 20 HP by default, with no status effects applied.
## `fishing`
Catch a set amount of items from the sea.
```yaml
fishing:
  type: "fishing"
  amount: 10                            # amount of fish caught
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `fishingcertain`
Catch a set amount of a specific item from the sea.
```yaml
fishingcertain:
  type: "fishingcertain"
  item: PUFFERFISH                      # type of item caught
  amount: 10                            # amount of item caught
  data: 3                               # (OPTIONAL) data code of item caught
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for item names. 
## `playerkilling`
Kill a set amount of players.
```yaml
playerkilling:
  type: "playerkilling"
  amount: 10                            # amount of players killed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `walking`
Walk a set distance.
```yaml
walking:
  type: "walking"
  distance: 1000                        # distance in blocks travelled
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `inventory`
Obtain a set of items.
```yaml
beef:
  type: "inventory"
  item: RAW_BEEF                        # name of item (can be id or minecraft name)
  amount: 8                             # amount of item needed
  data: 0                               # (OPTIONAL) data code
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
The `data` field may not work on newer Spigot versions. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for item names. 

**First alternative layout**

This alternative layout allows for more exact item specification, with a certain name, lore, enchantments etc.
```yaml
beef:
  type: "inventory"
  item:                                 # SPECIFIC item with name and lore
    name: "&cSpecial Beef"
    type: "RAW_BEEF"
    lore:
     - "&7This is a special type of beef"
  amount: 8                             # amount of item needed
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Please see [Defining items](https://github.com/LMBishop/Quests/wiki/Defining-items) for more information on specifying items within Quests, and for instructions on how to add different attributes such as unbreakable or hidden enchantments.

**Second alternative layout**

This alternative layout allows for referencing [quest items](Defining-items#quest-items).
```yaml
beef:
  type: "inventory"
  item:                                 # USING quest-item
    quest-item: "specialbeef"
  amount: 8                             # amount of item needed
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Please see [Defining items § Quest items](Defining-items#quest-items) for more information on using quest items.
## `crafting`
Craft a specific item.
```yaml
compass:
  type: "crafting"
  item: COMPASS                         # name of item (can be id or minecraft name)
  amount: 5                             # amount of item needed
  data: 0                               # (OPTIONAL) data code
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
The `data` field may not work on newer Spigot versions. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for item names.

**First alternative layout**

The alternative layout allows for more exact item specification, with a certain name, lore, enchantments etc.
```yaml
compass:
  type: "crafting"
  item:                                 # SPECIFIC item with name and lore
    name: "&cSuper Compass"
    type: "COMPASS"
    lore:
     - "&7This is special compass with a fancy name"
  amount: 5                             # amount of item needed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Please see [Defining items](https://github.com/LMBishop/Quests/wiki/Defining-items) for more information on specifying items within Quests, and for instructions on how to add different attributes such as unbreakable or hidden enchantments.

**Second alternative layout**

This alternative layout allows for referencing [quest items](Defining-items#quest-items).
```yaml
beef:
  type: "crafting"
  item:                                 # USING quest-item
    quest-item: "supercompass"
  amount: 5                             # amount of item needed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Please see [Defining items § Quest items](Defining-items#quest-items) for more information on using quest items.

## `consume`
Consume a specific item.
```yaml
beef:
  type: "consume"
  item: RAW_BEEF                        # name of item (can be id or minecraft name)
  amount: 8                             # amount of item consumed
  data: 0                               # (OPTIONAL) data code
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
The `data` field may not work on newer Spigot versions. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for item names. 

**First alternative layout**

This alternative layout allows for more exact item specification, with a certain name, lore, enchantments etc.
```yaml
beef:
  type: "consume"
  item:                                 # SPECIFIC item with name and lore
    name: "&cSpecial Beef"
    type: "RAW_BEEF"
    lore:
     - "&7This is a special type of beef"
  amount: 8                             # amount of item consumed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Please see [Defining items](https://github.com/LMBishop/Quests/wiki/Defining-items) for more information on specifying items within Quests, and for instructions on how to add different attributes such as unbreakable or hidden enchantments.

**Second alternative layout**

This alternative layout allows for referencing [quest items](Defining-items#quest-items).
```yaml
beef:
  type: "inventory"
  item:                                 # USING quest-item
    quest-item: "specialbeef"
  amount: 8                             # amount of item needed
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Please see [Defining items § Quest items](Defining-items#quest-items) for more information on using quest items.


## `bucketfill`
Fill a bucket a certain amount of times.
```yaml
bucketfill:
  type: "bucketfill"
  bucket: LAVA_BUCKET                   # bucket to fill
  amount: 8                             # amount of times to fill
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

## `bucketempty`
Empty a bucket a certain amount of times.
```yaml
bucketempty:
  type: "bucketempty"
  bucket: LAVA_BUCKET                   # bucket to empty
  amount: 8                             # amount of times to empty
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

## `enchanting`
Enchant a certain amount of items.
```yaml
enchanting:
  type: "enchanting"
  amount: 5                             # amount of items enchanted
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `expearn`
Earn a set amount of exp after starting the quest.
```yaml
expearn:
  type: "expearn"
  amount: 20                            # amount of experience earned
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `position`
Reach a set of co-ordinates.
```yaml
position:
  type: "position"
  x: 0                                  # x position
  y: 0                                  # y position
  z: 0                                  # z position
  world: world                          # name of world
  distance-padding: 10                  # (OPTIONAL) padding zone in meters/blocks - this will allow within 10 blocks of 0, 0, 0 - default = 0
```
The `distance-padding` option allows players to not have to stand in the exact position. It is recommended to have it set to at least 1.
## `distancefrom`
Be a set distance away from certain co-ordinates.
```yaml
distancefrom:
  type: "distancefrom"
  x: 0                                  # x position
  y: 0                                  # y position
  z: 0                                  # z position
  world: world                          # name of world
  distance: 10                          # required distance from coordinates
```
The `distance` is measured in a circle around the co-ordinates.
## `playtime`
Play for a certain amount of time.
```yaml
playtime:
  type: "playtime"
  minutes: 10                           # amount of minutes played
  ignore-afk: false                     # (OPTIONAL) ignore players marked as AFK by essentials
```
## `breeding`
Breed a set amount of animals.
```yaml
breeding:
  type: "breeding"
  amount: 5                             # amount of animals bred
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `farming`
**Requires 1.13+**  
Farm a set amount of any crop. For versions under 1.13, use [blockbreakcertain](#blockbreakcertain) with data codes.
```yaml
farming:
  type: "farming"
  amount: 10                            # amount of blocks to be broken
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `farmingcertain`
**Requires 1.13+**  
Farm a set amount of a specific crop. For versions under 1.13, use [blockbreakcertain](#blockbreakcertain) with data codes.
```yaml
farming:
  type: "farmingcertain"
  amount: 10                            # amount of blocks to be brkoen
  block: WHEAT                          # name of block (can be id or minecraft name)
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) for item names. 

**Alternative layout**

The alternative layout allows multiple blocks to be specified, which all count towards the progress.
```yaml
farmingmultiple:
  type: "farmingcertain"
  amount: 10                            # amount of blocks to be placed
  blocks:                               # name of blocks which will count towards progress
   - WHEAT
   - BEETROOT                           
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `brewing`
Brew a set amount of potions.
```yaml
brewing:
  type: "brewing"
  amount: 10                            # amount of potions brewed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `milking`
Milk a set amount of cows.
```yaml
milking:
  type: "milking"
  amount: 10                            # amount of cows milked
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `shearing`
Shear a set amount of sheep.
```yaml
shearing:
  type: "shearing"
  amount: 10                            # amount of sheep sheared
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `taming`
Tame a set amount of animals.
```yaml
taming:
  type: "taming"
  amount: 10                            # amount of mobs tamed
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `permission`
Test if a player has a permission.
```yaml
permission:
  type: "permission"
  permission: "some.permission.name"    # permission required to be marked as complete
```
## `command`
Execute a specific command.
```yaml
command:
  type: "command"
  command: "help"                       # command to execute
  ignore-case: true                     # (OPTIONAL) ignore capitalisation  - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
This task may not work for commands not registered (i.e commands made with plugins like DeluxeMenu).
## `citizens_deliver`
Deliver a set of items to a NPC.
```yaml
citizensdeliver:
  type: "citizens_deliver"
  npc-name: "Gerald"                    # name of NPC
  item: RAW_BEEF                        # name of item (can be id or minecraft name)
  amount: 8                             # amount of item needed
  data: 0                               # (OPTIONAL) data code
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
The `data` field may not work on newer Spigot versions. Please see [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) (1.13+) or [this list](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) (1.8-1.12) for item names. 

**First alternative layout**

The alternative layout allows for more exact item specification, with a certain name, lore, enchantments etc.
```yaml
beef:
  type: "citizens_deliver"
  npc-name: "Gerald"                    # name of NPC
  item:                                 # SPECIFIC item with name and lore
    name: "&cSpecial Beef"
    type: "RAW_BEEF"
    lore:
     - "&7This is a special type of beef"
  amount: 8                             # amount of item needed
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Please see [Defining items](https://github.com/LMBishop/Quests/wiki/Defining-items) for more information on specifying items within Quests, and for instructions on how to add different attributes such as unbreakable or hidden enchantments.

**Second alternative layout**

This alternative layout allows for referencing [quest items](Defining-items#quest-items).
```yaml
beef:
  type: "citizens_deliver"
  npc-name: "Gerald"                    # name of NPC
  item:                                 # USING quest-item
    quest-item: "specialbeef"
  amount: 8                             # amount of item needed
  remove-items-when-complete: false     # (OPTIONAL) take the items away from the player on completion - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
Please see [Defining items § Quest items](Defining-items#quest-items) for more information on using quest items.
## `citizens_interact`
Interact with (right-click) an NPC.
```yaml
citizensinteract:
  type: "citizens_interact"
  npc-name: "Gerald"                    # name of NPC
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
## `askyblock_level`
Reach a certain island level using ASkyBlock.
```yaml
askyblock:
  type: "askyblock_level"
  level: 10                             # island level needed
```
## `uskyblock_level`
Reach a certain island level using uSkyBlock.
```yaml
uskyblock:
  type: "uskyblock_level"
  level: 10                             # island level needed
```
## `mythicmobs_killing`
Kill a set amount of a MythicMobs entity.
```yaml
mythicmobs:
  type: "mythicmobs_killing"
  amount: 1                             # amount of mobs to be killed
  name: "SkeletalKnight"                # internal name of mob (name in config - NOT display name)
  level: 1                              # (OPTIONAL) the exact level the mob must be for it to count
  min-level: 1                          # (OPTIONAL) the minimum level the mob must be for it to count
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
The `level` and `min-level` fields should not be specified together. The `name` field is the name of the mob in the configuration (i.e the name you use to spawn the mob).
## `bentobox_level`
Reach a certain island level in the level addon for BentoBox.
```yaml
bentobox:
  type: "bentobox_level"
  level: 20                             # minimum island level needed
```
## `iridiumskyblock_value`
Reach a certain island value for IridiumSkyblock.
```yaml
iridiumskyblock:
  type: "iridiumskyblock_value"
  value: 20                             # minimum island value needed
```
## `placeholderapi_evaluate`
Parse any placeholder and evaluate its result.
```yaml
papieval:
  type: "placeholderapi_evaluate"
  placeholder: "%player_name%"          # placeholder string
  evaluates: "fatpigsarefat"            # what it should evaluate as to be marked as complete
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
**Alternative layout**

The alternative layout allows mathematical comparisons to be done on the placeholder result.
```yaml
papieval:
  type: "placeholderapi_evaluate"
  placeholder: "%player_empty_slots%"   # placeholder string
  evaluates: "20"                       # number to compare to
  operator: "GREATER_THAN"              # (OPTIONAL) numerical operator, "evaluates" MUST be an integer
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
The following values for `operator` are allowed: `GREATER_THAN` (>), `LESS_THAN` (<), `GREATER_THAN_OR_EQUAL_TO` (>=), `LESS_THAN_OR_EQUAL_TO` (<=). Ensure the outcome of `placeholder` is an integer.
## `essentials_balance`
Reach a certain balance.
```yaml
essentialsbalance:
  type: "essentials_balance"
  amount: 1000                          # amount of money to reach
```
## `essentials_moneyearn`
Earn a certain amount of money.
```yaml
essentialsbalance:
  type: "essentials_moneyearn"
  amount: 1000                          # amount of money to earn
```
## `shopguiplus_buycertain`
Buy something from a ShopGUI+ shop.
```yaml
shopguiplusbuy:
  type: "shopguiplus_buycertain"
  id: "item_id"                         # shopgui+ id of item to buy
  amount: 1000                          # amount of thing to buy
```
## `shopguiplus_sellcertain`
Sell something to a ShopGUI+ shop.
```yaml
shopguiplussell:
  type: "shopguiplus_sellcertain"
  id: "item_id"                         # shopgui+ id of item to sell
  amount: 1000                          # amount of thing to sell
```
## `fabledskyblock_level`
Reach a certain island level for FabledSkyblock.
```yaml
fabledskyblock:
  type: "fabledskyblock_level"
  level: 20                             # minimum island level needed
```
## `superiorskyblock_level`
Reach a certain island level for SuperiorSkyblock.
```yaml
superiorskyblock:
  type: "superiorskyblock_level"
  level: 20                             # minimum island level needed
```
## `superiorskyblock_worth`
Reach a certain island worth for SuperiorSkyblock.
```yaml
superiorskyblock:
  type: "superiorskyblock_worth"
  worth: 20000                          # minimum island worth needed
```
## `votingplugin_vote`
Vote a certain amount of times.
```yaml
votingplugin:
  type: "votingplugin_vote"
  amount: 5                             # minimum votes needed
```