<p align="center">
<img src="https://leonardobishop.com/~/artwork/questcompass2-256.png" width="200" height="200"><br>
<img src="https://img.shields.io/github/license/LMBishop/Quests">
<img src="https://img.shields.io/github/workflow/status/LMBishop/Quests/Build%20plugin">
<img src="https://img.shields.io/github/issues-raw/LMBishop/Quests">
<img src="https://img.shields.io/spiget/version/23696?color=inactive&label=version">
<img src="https://mcbadges.leonardobishop.com/all/downloads?spigot=23696&songoda=quests-quests&polymart=938">
<br>
<h1 align="center">Quests</h1>
</p>

#### Quick Navigation
- [Downloads / Building](#-downloads--building)
- [Contributors](#-contributors)
- [Support](#-support)
- [License](#-license)
- [Wiki](#-wiki)

## 💾 Downloads / Building
The latest release version of Quests can be found [here](https://github.com/LMBishop/Quests/wiki/download).
The latest build of Quests (development version) can be found on [GitHub](https://github.com/LMBishop/Quests/actions).

<img src="https://mcbadges.leonardobishop.com/quests.svg"/>

Alternatively, you can build Quests via Gradle. Release versions of Quests are built using **Gradle**, targeting **Java 17**. You can change the target version in ``build.gradle``.
* Ensure Java is installed on your machine
* Clone this repository
* Run ``./gradlew`` (Linux and macOS) or ``gradlew`` (Windows) in the base directory to build Quests
    * The jar will be output in `/build/libs`

*See [CONTRIBUTING.md](https://github.com/LMBishop/Quests/blob/master/CONTRIBUTING.md) for more information.*


#### 🧰 Custom Task
Creating new Task Types within Quests is supported, [see the wiki](https://github.com/LMBishop/Quests/wiki/New-Task-Type) for help.

Quests can be found on the Maven repository listed below, or alternatively on [JitPack](https://jitpack.io/#LMBishop/Quests).

For versions from `repo.leonardobishop.com`, the **version number corresponds to the release version**. Please see Spigot for the latest release number.
#### 👨‍💻 Maven
```xml
<repository>
    <id>repo.leonardobishop.com</id>
    <url>https://repo.leonardobishop.com/releases/</url>
</repository>

<dependency>
    <groupId>com.leonardobishop</groupId>
    <artifactId>quests</artifactId>
    <version><!--LATEST SPIGOT VERSION--></version>
    <scope>provided</scope>
</dependency>
```

#### 👩‍💻 Gradle
```groovy
repositories {
    maven { url = uri('https://repo.leonardobishop.com/releases/') }
}

dependencies {
    compileOnly 'com.leonardobishop:quests:<LATEST SPIGOT VERSION>'
}
```

## 👫 Contributors
See https://github.com/LMBishop/Quests/graphs/contributors

#### 🤝 Contributing to Quests
See [CONTRIBUTING.md](https://github.com/LMBishop/Quests/blob/master/CONTRIBUTING.md)

## 📖 Wiki
Quests provides a highly detailed and well maintained [wiki](https://github.com/LMBishop/Quests/wiki/). The information there is very useful to new users of the plugin.

#### 🌟 Highlighted Articles
- [FAQs](https://github.com/LMBishop/Quests/wiki)
- [Tips](https://github.com/LMBishop/Quests/wiki/Tips)
- [Basic options](https://github.com/LMBishop/Quests/wiki/Basic-Options)
- [Creating a quest or category](https://github.com/LMBishop/Quests/wiki/Creating-A-Quest-Or-Category)
- [Custom GUI items](https://github.com/LMBishop/Quests/wiki/Custom-GUI-Items)
- [Task types](https://github.com/LMBishop/Quests/wiki/Task-Types)
- [PlaceholderAPI](https://github.com/LMBishop/Quests/wiki/PlaceholderAPI)

## 💡 Support
For support please open a [GitHub issue](https://github.com/LMBishop/Quests/issues) or join our [Discord server](https://discord.gg/mQ2RcJC). Please provide information of the issue, any errors that may come up and make sure you are using the latest version of the plugin.

#### ⁉️ Issue Tracker
**This is the preferred method of bug reporting & feature requests**. Please use one of the two templates which are provided. If it is neither a bug report or a feature request and is a question, Discord would be a better place to ask this instead.

#### 💬 Discord
**This is the preferred method for general questions about Quests or the development of the project**. There is no dedicated support team, rather a team of volunteers (myself) who can help only when they have time.

#### 🌐 Language
Please speak English and do not use any vulgar or harmful language. We work on this project in our free time, getting mad at us, making demands, or just complaining in general will not achieve anything.

## 📜 License
The **source code** for Quests is licensed under the GNU General Public License v3.0, to view the license click [here](https://github.com/LMBishop/Quests/blob/master/LICENSE.txt).

The **artwork** for Quests is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License ![](https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png), to learn more click [here](https://creativecommons.org/licenses/by-nc-sa/4.0/).
