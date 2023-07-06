---
title: votingplugin_vote
parent: External task types
grand_parent: Task types
---

# votingplugin_vote (task type)

Since v3.7
{: .label .label-green }

Plugin 'VotingPlugin' required
{: .label }

Vote a number of times using VotingPlugin.

## Options

| Key      | Description          | Type    | Required | Default | Notes |
|----------|----------------------|---------|----------|---------|-------|
| `amount` | The number of votes. | Integer | Yes      | \-      | \-    |

## Examples

Vote 10 times:

``` yaml
votingplugin:
  type: "votingplugin_vote"
  amount: 10                             # number of times to vote
```
