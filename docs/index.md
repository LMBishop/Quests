---
title: Home
nav_order: 1
---
<div style="float: right">
<img src="https://leonardobishop.com/~/artwork/questcompass2-256.png" width="140" height="140"><br>
</div>

# Quests documentation

Welcome to the Quests Wiki! Please use the sidebar for navigation around
the wiki.
{: .fs-6 .fw-300 }

---

{: .important }
The information on this wiki documents the most recent version of Quests,
and is built directly from the `docs` directory in the `master` branch on 
GitHub. It may contain documentation for features not yet released.

## New and highlighted articles

- [Tips](tips)
- [Basic options](configuration/basic-options)
- [Creating a quest](configuration/creating-a-quest)
- [Creating a category](configuration/creating-a-category)
- [Custom GUI items](configuration/custom-gui-items)
- [Task types](task-types)
- [PlaceholderAPI](tools/placeholderapi)

Spot a mistake or ambiguous information? Please consider [contributing
to the wiki](contributing-to-the-wiki).

## FAQs

**Q. What task types are there?**  
  
Many [task types](task-types) are provided with the plugin. Follow
the links there to see how to format them.

Some task types require another plugin to activate.

<!-- -->

**Q. Can I use this plugin on a BungeeCord network/use MySQL?**  
  
Yes, refer to [storage providers](configuration/storage-providers).

<!-- -->

**Q. Players are able to place blocks then break them to advance tasks! How do I stop this?**  
  
Take a look at the options for
[blockbreakcertain](task-types/blockbreak-(task-type)).
You can enable CoreProtect functionality to detect if a player has
placed a block, or enable the `reverse-if-broken` flag which will
subtract from task progress if a block is placed.

<!-- -->

**Q. How do I limit a quest to a certain world?**  
  
Most task types support world restrictions. Take a look at the task
configuration in [task types](task-types) to see if your task does.

<!-- -->

**Q. How do I organise quests in the GUI?**  
  
Quests can have a `sort-order`, which organises them within the GUI. You
can see it at [creating a quest](configuration/creating-a-quest).
  
If you want to add custom elements to a quest GUI, you can refer to
[custom GUI items](configuration/custom-gui-items).

<!-- -->

**Q. How to I only let specific players do specific quests?**  
  
Specific quests can have permissions enabled for them. Take a look at
[creating a quest](configuration/creating-a-quest) for more information.
Alternatively, entire categories can have permissions enabled.

<!-- -->

**Q. Does this plugin support PlaceholderAPI?**  

Yes, see [PlaceholderAPI](tools/placeholderapi).

<!-- -->

**Q. How do I get quest progress on a scoreboard?**  

I have written a guide for this: [Quest progress in
scoreboard](guides/quest-progress-in-scoreboard)

<!-- -->

**Q. I found a bug or have a feature request! Where do I report it?**  

Great! Go to the [issue
tracker](https://github.com/LMBishop/Quests/issues) to report bugs or
create feature requests. Please avoid using Spigot's discussion pages or
Discord for requests as I will most likely forget about it!

<!-- -->

**Q. I have a sizeable portion of money and I feel the need to donate it to you, where do I go?**  

Consider giving your money to [Mind](https://www.mind.org.uk/donate);
[Help for Heroes](https://www.helpforheroes.org.uk/donate-online/);
[Cancer Research UK](https://www.cancerresearchuk.org/); or, any other
good cause in your country/local area. Or, if you are programmatically
gifted, donating some of [your
time](https://github.com/LMBishop/Quests/pulls) instead.
