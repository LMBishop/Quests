---
title: Data migration tool
parent: Tools
---

# Data migration tool

The **data migration tool** is a tool that allows you to migrate your
data from one [storage provider](../configuration/storage-providers) to
another. This can also be used as a backup tool. The tool can be
accessed using `/quests admin migratedata`, which will generate a file
[migrate_data.yml](https://github.com/LMBishop/Quests/blob/master/bukkit/src/main/resources/resources/bukkit/migrate_data.yml),
where you must configure both providers.

The `from` section is the configuration for the storage provider you are
migrating from. The `to` section is the configuration for the storage
provider you are migrating to. Both sections are required.

When you have entered the information for both systems, you must set the
`ready` flag to **true** at the end of the file. Then, to execute the
migration, run the following command:

     /quests admin migratedata execute

  
{: .warning }
**It is advised that you do this process on a server with no players
online.** You should set a whitelist, or turn on maintenence mode,
before migrating data, and these commands should be done through your
server console. Trying this process with players online may result in
unexpected behaviour, or worse, potential data corruption!

Once the migration has finished, you can safely delete migrate_data.yml.
You may also want to manually update your main configuration to point to
the new data provider.
