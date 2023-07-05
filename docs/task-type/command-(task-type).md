Execute a specific command.

This task may not work for commands not properly registered with the
server (e.g. commands made with plugins like DeluxeMenu).

## Options

| Key           | Description                                          | Type                | Required | Default | Notes |
|---------------|------------------------------------------------------|---------------------|----------|---------|-------|
| `command`     | The command to execute.                              | String              | Yes      | \-      | \-    |
| `ignore-case` | Whether the casing of the command should be ignored. | Boolean             | No       | false   | \-    |
| `worlds`      | Worlds which should count towards the progress.      | List of world names | No       | \-      | \-    |

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
