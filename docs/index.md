---
title: Home
nav_order: 1
---
<div style="float: right">
<img src="https://leonardobishop.com/~/artwork/questcompass2-256.png" width="200" height="200"><br>
</div>

# Quests documentation

Welcome to the Quests Wiki! Please use the sidebar for navigation around
the wiki.

**🌟 New & Highlighted Articles**

- [Tips](tips "wikilink")
- [Basic options](Basic_options "wikilink")
- [Creating a quest](Creating_a_quest "wikilink")
- [Creating a category](Creating_a_category "wikilink")
- [Custom GUI items](Custom_GUI_items "wikilink")
- [Task types](Task_types "wikilink")
- [PlaceholderAPI](tools/placeholderapi.md "wikilink")

Spot a mistake or ambiguous information? Please consider [contributing
to the wiki](contributing_to_the_wiki "wikilink").

## FAQs

Q. What task types are there?  

<!-- -->

  
Many different [task types](task_types "wikilink") are provided. Follow
the links there to see how to format them.

<!-- -->

Q. Can I use this plugin on a BungeeCord network/use MySQL?  

<!-- -->

  
Yes, refer to [storage providers](storage_providers "wikilink").

<!-- -->

Q. Players are able to place blocks then break them to advance tasks! How do I stop this?  

<!-- -->

  
Take a look at the [Task configuration layout §
blockbreakcertain](Task_configuration_layout#blockbreakcertain "wikilink").
You can enable CoreProtect functionality to detect if a player has
placed a block, or enable the `reverse-if-broken` flag which will
subtract from task progress if a block is placed.

<!-- -->

Q. How do I limit a quest to a certain world?  

<!-- -->

  
Most task types support world restrictions. Take a look at the task
configuration in [task configuration
layout](task_configuration_layout "wikilink") to see if your task does.

<!-- -->

Q. How do I organise quests in the GUI?  

<!-- -->

  
Quests can have a `sort-order`, which organises them within the GUI. You
can see it at [creating a quest](creating_a_quest "wikilink").

<!-- -->

  
If you want to add custom elements to a quest GUI, you can refer to
[custom GUI items](custom_GUI_items "wikilink").

<!-- -->

Q. How to I only let specific players do specific quests?  

<!-- -->

  
Specific quests can have permissions enabled for them. Take a look at
[creating a quest](creating_a_quest "wikilink") for more information.
Alternatively, entire categories can have permissions enabled.

<!-- -->

Q. Does this plugin support PlaceholderAPI?  

<!-- -->

  
Yes, see [PlaceholderAPI](tools/placeholderapi.md "wikilink").

<!-- -->

Q. How do I get quest progress on a scoreboard?  

<!-- -->

  
I have written a guide for this: [Quest progress in
scoreboard](Quest_progress_in_scoreboard "wikilink")

<!-- -->

Q. I found a bug or have a feature request! Where do I report it?  

<!-- -->

  
Great! Go to the [issue
tracker](https://github.com/LMBishop/Quests/issues) to report bugs or
create feature requests. Please avoid using Spigot's discussion pages or
Discord for requests as I will most likely forget about it!

<!-- -->

Q. I have a sizeable portion of money and I feel the need to donate it to you, where do I go?  

<!-- -->

  
Consider giving your money to [Mind](https://www.mind.org.uk/donate);
[Help for Heroes](https://www.helpforheroes.org.uk/donate-online/);
[Cancer Research UK](https://www.cancerresearchuk.org/); or, any other
good cause in your country/local area. Or, if you are programmatically
gifted, donating some of [your
time](https://github.com/LMBishop/Quests/pulls) instead.
