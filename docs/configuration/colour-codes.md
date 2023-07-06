---
title: Colour codes
parent: Configuration
nav_order: 8
---
# Colour (color) codes

You can use colour codes anywhere the plugin accepts a message (plugin messages, display items, and in task configurations themselves).

The following table shows the colour capabilities of specific server versions:

|              | Before 1.16 | 1.16+ |
|--------------|-------------|-------|
| Colour Codes | ✔️          | ✔️    |
| Hexadecimal  | ❌           | ✔️    |

## Colour codes
The plugin will automatically translate colour codes from '&' to '§' for you.

| Name          | Chat Code | Hex Equivalent |
|---------------|-----------|----------------|
| Black         | `&0`      | #000000        |
| Dark Blue     | `&1`      | #0000AA        |
| Dark Green    | `&2`      | #00AA00        | 
| Dark Aqua     | `&3`      | #00AAAA        |
| Dark Red      | `&4`      | #AA0000        | 
| Dark Purple   | `&5`      | #AA00AA        | 
| Gold          | `&6`      | #FFAA00        | 
| Gray          | `&7`      | #AAAAAA        | 
| Dark Gray     | `&8`      | #555555        |
| Blue          | `&9`      | #5555FF        | 
| Green         | `&a`      | #55FF55        |
| Aqua          | `&b`      | #55FFFF        |
| Red           | `&c`      | #FF5555        |
| Light Purple  | `&d`      | #FF55FF        |
| Yellow        | `&e`      | #FFFF55        |
| White         | `&f`      | #FFFFFF        |
| Obfuscated    | `&k`      | -              |
| Bold          | `&l`      | -              |
| Strikethrough | `&m`      | -              |
| Underline     | `&n`      | -              |
| Italic        | `&o`      | -              |
| Reset         | `&r`      | -              |

## Hexadecimal colour
For compatible Minecraft versions, the plugin will also translate hex colour codes for you.

You can include a hex colour code as you would with a normal colour code: `&#<hex colour code>`. 

{: .important }
The `#` symbol indicates a hexadecimal colour code. These must be exactly six characters long.

For example, the following messages are identical:
```
&cThis is a red message.

&#FF5555This is a red message.
```
