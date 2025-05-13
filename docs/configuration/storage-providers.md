---
title: Storage providers
parent: Configuration
nav_order: 10
---

# Storage providers
{: .no_toc }

A **storage provider** is a source for player data
(sometimes referred to as quest progress files). Quests requires that
one storage system be configured to allow the plugin to initialise. If
there is no storage system configured, the plugin will default to
**yaml** storage. If there is an error for any reason during the
initialisation of a storage system, the plugin will be disabled.

- YAML (`yaml`)
- MySQL (`mysql`)

When changing storage systems, **the plugin must be restarted for the
changes to have effect**.

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

## Supported storage systems

### Flatfile

#### YAML

Storing player data in YAML files is the default storage method in
Quests, and is a type of 'flatfile' storage.

``` yaml
options:
  # ...
  storage:
    provider: "yaml"
```

Player data can be found inside Quests/playerdata/ and can be modified
as long as the server is not running. It is not recommended to try and
alter these files while the server is online, as this could cause data
consistency issues.

### Network

{: .warning } 
> ️**Using Quests on a BungeeCord network may lead to a possible race
> condition.** Allowing players to to connect directly to another server
> running Quests may result in the new server loading old data. This
> happens because BungeeCord establishes a connection with the new server 
> before disconnecting the player from the old one, leading to the new 
> server loading player data before the old server has saved it.
> 
> Quests offers a workaround, which is to [delay the loading of player
> data](#delay-loading). You may also want to
> consider forcing players to switch servers through a hub server, or
> decreasing the autosave period. In either case, the race condition still
> exists; there is not an easy way to coordinate the loading/saving due to
> how BungeeCord works. **You must understand this warning before using
> Quests in this way.**

#### MySQL

Quests can connect to and store player data in a MySQL database. This is
particularly useful if you want to have multiple servers use the same
player data.

``` yaml
options:
  # ...
  storage:
    provider: "mysql"
```

You must also configure the plugin to connect to the database.

``` yaml
      database-settings:
        network:
          database: "minecraft"
          username: "root"
          password: ""
          address: "localhost:3306"
```

The database specified **must** exist before connecting Quests to it.
The address is given in the following format: ip:port (e.g
127.0.0.1:3306).

There are also some other options you can configure, as Quests uses
HikariCP to manage its connections to the database. You can see
descriptions of each option on the [HikariCP
README](https://github.com/brettwooldridge/HikariCP).

``` yaml
        connection-pool-settings:
        minimum-idle: 8
        maximum-pool-size: 8
        connection-timeout: 5000
        idle-timeout: 600000
        keepalive-time: 0
        maximum-lifetime: 1800000
        data-source-properties: {}
        table-prefix: "quests_"
```

## Data synchronisiation

### Delay loading

Quests offers a workaround to the [race
condition](#network), which is to delay the
loading of player data in hopes that the server before has enough time
to save the data.

You can enable this in your config here:

``` yaml
options:
  # ...
  storage:
    provider: "mysql"
    synchronisation:
      delay-loading: 0 # (ticks - change to any value above 0)
    # ...
```

A value of 50 (2.5 seconds) should be enough for most servers, however
you may want to increase it if, for example, your database is not on the
same network as your Minecraft server. Again, this **does not solve the
race condition**, but it should help mitigate it.

See the issue in the issue tracker:
[Issue 180](https://github.com/LMBishop/Quests/issues/180)
