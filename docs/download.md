---
title: Download
nav_order: 2
---
# Download

Release builds of Quests are officially distributed at the
sites on this page. Sources not listed here may contain modified, or
outdated versions.

## Release builds

- [SpigotMC
  (preferred)](https://www.spigotmc.org/resources/quests-1-8-1-19-set-up-goals-for-players.23696/)
- [Hangar](https://hangar.papermc.io/LMBishop/Quests)
- [Modrinth](https://modrinth.com/mod/quests)
- [GitHub](https://github.com/LMBishop/Quests/releases)
- [Polymart](https://polymart.org/resource/quests.938)
- [~~Songoda~~](https://songoda.com/marketplace/product/quests-quests.544)

Some sources may be out of date. Please check the version number
before downloading.

## Development builds

- [GitHub Actions](https://github.com/LMBishop/Quests/actions)

Development builds are automatically built by GitHub. You may need a
GitHub account to see or download these artifacts.

Instructions on building Quests is also provided here:
<https://github.com/LMBishop/Quests/blob/master/CONTRIBUTING.md>

## Which JAR should I download?

{: .note }
The version you need to download depends on the server version and software.

Starting with Minecraft version 1.12, the minimum required version
of Java became Java 1.8. In version 1.17, the minimum requirement
was temporarily raised to Java 16. From version 1.18 onward, Java 17
became the minimum requirement. With the 1.20.5 update, Java 21
became the required version.

However, these are only the [minimum requirements](https://minecraft.wiki/w/Tutorials/Update_Java#Why_update?).
Since version 1.13.2, Spigot has included the ASM dependency, which allows for bytecode
manipulation while also [limiting the maximum version of Java](https://asm.ow2.io/versions.html)
that a plugin can use for proper processing by ASM. Additionally, for some versions
of Paper running on higher-than-recommended versions of Java, it may be necessary to use
[`-DPaper.IgnoreJavaVersion=true`](https://docs.papermc.io/paper/reference/system-properties#paperignorejavaversion)
system property (add it to the startup script directly after `java`) to bypass Java version checks.

Below is a table containing three columns. The first lists the Spigot API versions.
The second provides the version that corresponds to both the required Java version
and the compatible Quests plugin version. The third column includes unofficial
working alternatives—either newer or older setups—that are generally experimental
but expected to work.

| Spigot API version                                                                                                                                                                                                                                                                                                                                                                                   | Suggested Java and Quests version | Unofficial working alternatives                                                                     |
|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------|-----------------------------------------------------------------------------------------------------|
| 1.8-R0.1-SNAPSHOT<br>1.8.3-R0.1-SNAPSHOT<br>1.8.4-R0.1-SNAPSHOT<br>1.8.5-R0.1-SNAPSHOT<br>1.8.6-R0.1-SNAPSHOT<br>1.8.7-R0.1-SNAPSHOT<br>1.8.8-R0.1-SNAPSHOT                                                                                                                                                                                                                                          | 1.8                               | Spigot: 21, 17, 11<br>Paper: 11 (limit: [JDK-8210522](https://bugs.openjdk.org/browse/JDK-8210522)) |
| 1.9-R0.1-SNAPSHOT<br>1.9.2-R0.1-SNAPSHOT<br>1.9.4-R0.1-SNAPSHOT<br>1.10-R0.1-SNAPSHOT<br>1.10.2-R0.1-SNAPSHOT<br>1.11-R0.1-SNAPSHOT<br>1.11.1-R0.1-SNAPSHOT<br>1.11.2-R0.1-SNAPSHOT<br>1.12-pre2-SNAPSHOT<br>1.12-pre5-SNAPSHOT<br>1.12-pre6-SNAPSHOT<br>1.12-R0.1-SNAPSHOT<br>1.12.1-R0.1-SNAPSHOT<br>1.12.2-R0.1-SNAPSHOT<br>1.13-pre7-R0.1-SNAPSHOT<br>1.13-R0.1-SNAPSHOT<br>1.13.1-R0.1-SNAPSHOT | 1.8                               | 21, 17, 11                                                                                          |
| 1.13.2-R0.1-SNAPSHOT<br>1.14-pre5-SNAPSHOT<br>1.14-R0.1-SNAPSHOT<br>1.14.1-R0.1-SNAPSHOT<br>1.14.2-R0.1-SNAPSHOT<br>1.14.3-SNAPSHOT<br>1.14.3-pre4-SNAPSHOT<br>1.14.3-R0.1-SNAPSHOT<br>1.14.4-R0.1-SNAPSHOT<br>1.15-R0.1-SNAPSHOT<br>1.15.1-R0.1-SNAPSHOT<br>1.15.2-R0.1-SNAPSHOT<br>1.16.1-R0.1-SNAPSHOT<br>1.16.2-R0.1-SNAPSHOT<br>1.16.3-R0.1-SNAPSHOT<br>1.16.4-R0.1-SNAPSHOT                    | 11                                | 1.8 (limit: [ASM 7.1 - 8.0.1](https://asm.ow2.io/versions.html))                                    |
| 1.16.5-R0.1-SNAPSHOT<br>1.17-R0.1-SNAPSHOT<br>1.17.1-R0.1-SNAPSHOT<br>1.18-rc3-R0.1-SNAPSHOT<br>1.18-pre5-R0.1-SNAPSHOT<br>1.18-pre8-R0.1-SNAPSHOT<br>1.18-R0.1-SNAPSHOT<br>1.18.1-R0.1-SNAPSHOT<br>1.18.2-R0.1-SNAPSHOT<br>1.19-R0.1-SNAPSHOT<br>1.19.1-R0.1-SNAPSHOT<br>1.19.2-R0.1-SNAPSHOT<br>1.19.3-R0.1-SNAPSHOT<br>1.19.4-R0.1-SNAPSHOT                                                       | 17                                | 11, 1.8 (limit: [ASM 9.1 - 9.4](https://asm.ow2.io/versions.html))                                  |
| 1.20-R0.1-SNAPSHOT<br>1.20.1-experimental-SNAPSHOT<br>1.20.1-R0.1-SNAPSHOT<br>1.20.2-experimental-SNAPSHOT<br>1.20.2-R0.1-SNAPSHOT<br>1.20.3-R0.1-SNAPSHOT<br>1.20.4-experimental-SNAPSHOT<br>1.20.4-R0.1-SNAPSHOT<br>1.20.5-R0.1-SNAPSHOT<br>1.20.6-experimental-SNAPSHOT<br>1.20.6-R0.1-SNAPSHOT<br>1.21-R0.1-SNAPSHOT<br>1.21.1-R0.1-SNAPSHOT                                                     | 21                                | 17, 11, 1.8 (limit: [ASM 9.5 - 9.7](https://asm.ow2.io/versions.html))                              |

## License

The full license text is available here:
<https://github.com/LMBishop/Quests/blob/master/LICENSE.txt>

    Quests

    Copyright (C) 2022  Leonardo Bishop and contributors

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see [https://www.gnu.org/licenses].
