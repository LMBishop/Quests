---
title: Contributing to the wiki
nav_order: 10
---

# Contributing to the wiki

If you spot any errors in the wiki, or want to add more information of
you own, then we would be happy to review changes. This documentation 
is built directly from 
[the `docs` directory on the `master` branch on GitHub](https://github.com/LMBishop/Quests/tree/master/docs),
using GitHub pages. 

## Submitting edits

{: .new }
It is no longer necessary to submit edits through the issue tracker,
as documentation is now maintained within the repository itself.

If you want to add information or edit the wiki, please fork this
repository and make your changes.

If you need guidance, check out the [CONTRIBUTING.md](https://github.com/LMBishop/Quests/blob/master/CONTRIBUTING.md)
file in the main repository.

## Editing guidelines

The Quests wiki loosely follows some conventions:

- Article titles and headers should be written in lower case, with only
  the first word being capitalised. The only exception to this is with
  names (e.g. PlaceholderAPI, AnimatedScoreboard).
- Headings should not used to restate the page name. Pages usually open
  with a short description at the top, they should never start with
  another heading.
- British English should be used.
- Top-level headings should start at H2 (`## Heading`). H1 (`# Heading`) 
  is reserved for the article title itself.
- Longer articles should have a table of contents.
- Incomplete pages should have a warning at the top stating so.
  You can transclude a pre-made banner by adding
  `{% raw %}{% include incomplete.md %}{% endraw %}` to the page.
- Most files have an 80 character line limit, to assist with readability
  in a split view, except links or source code / 
  configuration files. 

These conventions may or may not be wholly followed throughout the wiki,
though it would be beneficial for new pages and new revisions to follow
this convention.
