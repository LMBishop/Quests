---
title: placeholderapi_evaluate
parent: External task types
grand_parent: Task types
---

# placeholderapi_evaluate (task type)

Since v2.9.5
{: .label .label-green }

Plugin 'PlaceholderAPI' required
{: .label }

Evaluate a certain PlaceholderAPI placeholder and compare it against a given condition.

## Options

| Key             | Description                                                 | Type                | Required | Default | Notes                                                                                                                                                                                                  |
|-----------------|-------------------------------------------------------------|---------------------|----------|---------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `placeholder`   | The placeholder to evaluate.                                | String              | Yes      | \-      | \-                                                                                                                                                                                                     |
| `evaluates`     | The string the placeholder should evaluate to.              | String              | Yes      | \-      | \-                                                                                                                                                                                                     |
| `operator`      | The comparative operator to use for numeric placeholders.   | String              | No       | \-      | One of: `GREATER_THAN`, `LESS_THAN`, `GREATER_THAN_OR_EQUAL_TO`, `LESS_THAN_OR_EQUAL_TO`. The value in `evaluates` **must** be numeric. If this is not specified, then exact equality will be assumed. |
| `refresh-ticks` | How frequently the placeholder should be evaluated.         | Integer             | No       | \-      | This works in addition to the global option defined in `config.yml`.                                                                                                                                   |
| `async`         | Whether the placeholder should be evaluated asynchronously. | Boolean             | No       | \-      | \-                                                                                                                                                                                                     |                                                                                                                                                                                                     
| `worlds`        | Worlds which should count towards the progress.             | List of world names | No       | \-      | \-                                                                                                                                                                                                     |

## Examples

Have a player name of "fatpigsarefat":

``` yaml
papieval:
  type: "placeholderapi_evaluate"
  placeholder: "%player_name%"          # placeholder string
  evaluates: "fatpigsarefat"            # what it should evaluate as to be marked as complete
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```

Be online when there are more than 20 empty slots on the server:

``` yaml
papieval:
  type: "placeholderapi_evaluate"
  placeholder: "%player_empty_slots%"   # placeholder string
  evaluates: "20"                       # number to compare to
  operator: "GREATER_THAN"              # (OPTIONAL) numerical operator, "evaluates" MUST be an integer
  worlds:                               # (OPTIONAL) restrict to certain worlds
   - "world"
```
