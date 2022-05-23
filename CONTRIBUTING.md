We welcome all contributions, given they fit within the scope of Quests. If you're unsure, then ask me first!

## Building and updating Quests
* Ensure Java is installed on your machine
* Clone this repository
* Edit the source code as your please
* Run ``./gradlew`` (Linux and macOS) or ``gradlew`` (Windows) in the base directory to build Quests
  * The jar will be output in `/build/libs`

## Project structure
* `/common`: contains interfaces and abstract classes that are used, should remain platform independent
* `/bukkit`: contains implementations of the interfaces in `/common` and most of the plugin code for Bukkit
* `/bungee`: for BungeeCord, this isn't a Quests plugin, see [this issue](https://github.com/LMBishop/Quests/issues/180) for more info

### API
The best way to learn how the plugin works is to just look at the source code. 
Start from the main class (`BukkitQuestsPlugin`) and see how the plugin initialises itself.
Most classes are self-explanatory, and the main class holds instances and provides getters for basically every module.

## Contributing guidelines
If you plan on contributing upstream please note the following:
* Discuss **large** changes first
  * A large change significantly changes API and behaviour, such as major refactoring
* Take a look at how the rest of the project is formatted and follow that (usually 4 spaces)
* Do not alter the version number in ``build.gradle``, that will be done when the release version is ready
* Limit the first line of commit messages to ~50 chars and leave a space below that, with an optional extended description
* **Test your changes** on the latest Spigot version before making a pull request

## Guidance
If you have never contributed to an open source project, the general workflow is as follows:
1. Fork this repository
2. Clone your fork and make your changes
3. Test changes work
4. Commit your changes and rebase onto `upstream/master` if there have been updates since
   * This to keep a linear commit history and prevent me having to resolve merge conflicts
   * `upstream/master` refers to the original repository (LMBishop/Quests), you may have to add it as a remote yourself
5. Push local changes to any branch on your fork
6. Open a pull request from your branch to `master` 

By contributing to Quests you agree to license your code under the [GNU General Public License v3.0](https://github.com/LMBishop/Quests/blob/master/LICENSE.txt).
