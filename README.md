# Changes:

- Quests recognize [Atlas](https://github.com/JeracraftNetwork/Atlas) items
  - In case Quests break due to Atlas changes, change the JAR  recompile
- Items go to your [Stash](https://github.com/JeracraftNetwork/Stash) if inventory is full

## Building

- Clone this repository
- In case of updating - have the latest version of Atlas in local maven repository
  - Update `build.gradle` deps to latest versions
- Run `./gradlew` (Linux and macOS) or `gradlew` (Windows) in the base directory to build Quests
  - The jar will be output in `/build/libs`

## Deploy
- Deploy via `gradlew publish`
