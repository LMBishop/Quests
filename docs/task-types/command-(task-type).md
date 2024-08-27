---
title: command
parent: Built-in task types
grand_parent: Task types
---

# command (task type)

Since v2.12
{: .label .label-green }

Execute a specific command.

This task may not work for commands not properly registered with the
server (e.g. commands made with plugins like DeluxeMenu).

## Options

| Key                    | Description                                          | Type                       | Required | Default | Notes                                         |
|------------------------|------------------------------------------------------|----------------------------|----------|---------|-----------------------------------------------|
| `command` / `commands` | The command(s) to execute.                           | String, or list of strings | Yes      | \-      | \-                                            |
| `ignore-case`          | Whether the casing of the command should be ignored. | Boolean                    | No       | false   | \-                                            |
| `command-match-mode`   | The match mode to be used to compare the strings     | String                     | No       | EQUALS  | One of: `EQUALS`, `STARTS_WITH`, `ENDS_WITH`. |
| `worlds`               | Worlds which should count towards the progress.      | List of world names        | No       | \-      | \-                                            |

## Examples

Ask for help:

``` yaml
command:
  type: "command"
  command: "help"                       # command to execute
  ignore-case: true                     # (OPTIONAL) ignore capitalisation  - default: false
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
