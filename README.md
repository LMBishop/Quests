<div align="center">
<img align="center" src="https://leonardobishop.com/artwork/QUESTS%20BANNER%20NO%20BACKGROUND.png"></img><br>
</div>

[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/LMBishop/Quests.svg)](http://isitmaintained.com/project/LMBishop/Quests "Average time to resolve an issue") [![Percentage of issues still open](http://isitmaintained.com/badge/open/LMBishop/Quests.svg)](http://isitmaintained.com/project/LMBishop/Quests "Percentage of issues still open") ![Downloads](https://mc-download-badges.herokuapp.com/services/spigotsongoda/downloads.php?spigot=23696&songoda=quests-quests)
## About Quests
This plugin will allow your players to do quests in-game to earn rewards. This can all be configured.

## Downloads/Building
The latest release version of Quests can be found on [Spigot](https://www.spigotmc.org/resources/▶-quests-◀-set-up-goals-for-players.23696/).
The latest build of Quests (development version) can be found on [GitHub](https://github.com/LMBishop/Quests/actions).

Alternatively, you can build Quests via Gradle. Release versions of Quests are built using **Java 8**, you can change the target version in ``build.gradle``.
* ensure Java 8 is installed on your machine
* clone this repository
* run ``gradlew build`` in the base directory

### Custom Task
Creating new Task Types within Quests is supported, [see the wiki](https://github.com/LMBishop/Quests/wiki/New-Task-Type) for help.

You can include quests in your project using [JitPack](https://jitpack.io/#LMBishop/Quests) as a repository.

### Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.LMBishop</groupId>
    <artifactId>Quests</artifactId>
    <version>master-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

### Gradle
```groovy
repositories {
    maven { url = 'https://jitpack.io' }
}  
dependencies {
    compileOnly 'com.github.LMBishop:Quests:master-SNAPSHOT'
}
```

## Contributors
See https://github.com/LMBishop/Quests/graphs/contributors

## Support
For support please open a [GitHub issue](https://github.com/LMBishop/Quests/issues) or join our [Discord server](https://discord.gg/mQ2RcJC). Please provide information of the issue, any errors that may come up and make sure you are using the latest version of the plugin.

### Issue Tracker
**This is the preferred method of bug reporting & feature requests**. Please use one of the two templates which are provided. If it is neither a bug report or a feature request and is a question, Discord would be a better place to asked this instead. **Follow the template in the issue tracker**. There is nothing more frustrating than people not reporting a bug correctly by missing out vital steps to reproduce the bug or an incomplete description. If the issue is not correctly formatted, it will be closed and ignored.

### Discord
**This is the preferred method for general questions about Quests or the development of the project**. Join the Discord server and go to the relative support channel (in this case: #quests-support). Please provide steps to reproduce and a good enough description of the bug and include any errors you may see in console.

### Language
Please speak English and do not use any vulgar or harmful language. We work on this project in our free time, getting mad at us if things do not work will not achieve anything.

## License
The **source code** for Quests is licensed under the GNU General Public License v3.0, to view the license click [here](https://github.com/LMBishop/Quests/blob/master/LICENSE.txt).

The **artwork** for Quests is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License ![](https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png), to learn more click [here](https://creativecommons.org/licenses/by-nc-sa/4.0/).

## Configuration Assistance
The configuration documentation can be found at the [wiki](https://github.com/LMBishop/Quests/wiki/Creating-A-Quest-Or-Category).

## Contributing To Quests
See [CONTRIBUTING.md](https://github.com/LMBishop/Quests/blob/master/CONTRIBUTING.md)
