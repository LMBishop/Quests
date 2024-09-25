plugins {
    id("io.github.goooler.shadow")
}

tasks.build {
    dependsOn.add(tasks.shadowJar)
}

tasks.withType<ProcessResources> {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to project.version))
    }
}

repositories {
    // Paper
    maven("https://repo.papermc.io/repository/maven-public/")
    // Paper (adventure-bom snapshots)
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    // ASkyBlock, BentoBox, bStats, Citizens
    maven("https://repo.codemc.io/repository/maven-public/")
    // AuthLib
    maven("https://libraries.minecraft.net/")
    // CoreProtect
    maven("https://maven.playpro.com/")
    // EcoBosses, EcoMobs
    maven("https://repo.auxilor.io/repository/maven-public/")
    // EssentialsX
    maven("https://repo.essentialsx.net/releases/")
    // MMOItems, MythicLib
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    // MythicMobs 4, MythicMobs 5
    maven("https://mvn.lumine.io/repository/maven-public/")
    // NuVotifier
    maven("https://repo.leonardobishop.com/releases/")
    // Oraxen
    maven("https://repo.oraxen.com/releases")
    // PlaceholderAPI
    maven("https://repo.extendedclip.com/releases")
    // CustomFishing, ItemsAdder, SCore, ShopGUIPlus, Slimefun4, Vault
    maven("https://jitpack.io/")
    // PlayerPoints
    maven("https://repo.rosewooddev.io/repository/public/")
    // SuperiorSkyblock2
    maven("https://repo.bg-software.com/repository/api/")
    // uSkyBlock TODO fix whenever repo is up
    //maven("https://raw.githubusercontent.com/uskyblock/uskyblock-repo/master/")
    // VotingPlugin
    maven("https://nexus.bencodez.com/repository/maven-public/")
    // WildStacker
    maven("https://repo.bg-software.com/repository/api/")
    // ZNPCsPlus
    maven("https://repo.pyr.lol/snapshots")
    // BedWars1058
    maven("https://repo.andrei1058.dev/releases/")

    // bungeecord-chat, HikariCP, hppc, JetBrains Annotations, slf4j
    mavenCentral()
}

dependencies {
    // Quests common module
    compileOnlyProject(":common")

    // Paper
    compileOnlyServer("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    // ASkyBlock
    compileOnlyPlugin("com.wasteofplastic:askyblock:3.0.9.4")
    // AuthLib
    compileOnlyPlugin("com.mojang:authlib:1.5.21")
    // BentoBox
    compileOnly("world.bentobox:bentobox:2.5.4-SNAPSHOT")
    compileOnly("world.bentobox:level:2.16.1-SNAPSHOT")
    // Citizens
    compileOnlyPlugin("net.citizensnpcs:citizensapi:2.0.30-SNAPSHOT")
    // CoreProtect
    compileOnlyPlugin("net.coreprotect:coreprotect:21.2")
    // CustomFishing
    compileOnlyPlugin("com.github.Xiao-MoMi:Custom-Fishing:2.2.20")
    // EcoBosses, EcoMobs
    compileOnlyPlugin("com.willfp:EcoBosses:9.14.0")
    compileOnlyPlugin("com.willfp:EcoMobs:10.0.0-b1")
    compileOnlyPlugin("com.willfp:eco:6.65.1")
    compileOnlyPlugin("com.willfp:libreforge:4.21.1")
    // EssentialsX
    compileOnlyPlugin("net.essentialsx:EssentialsX:2.19.7")
    // IridiumSkyblock TODO fix whenever repo is up
    //compileOnlyPlugin("com.github.Iridium-Development:IridiumSkyblock:master-SNAPSHOT")
    // ItemsAdder
    compileOnlyPlugin("com.github.LoneDev6:API-ItemsAdder:3.5.0b")
    // JetBrains Annotations
    compileOnlyPlugin("org.jetbrains:annotations:24.1.0")
    // MMOItems
    compileOnlyPlugin("net.Indyuce:MMOItems-API:6.9.2-SNAPSHOT")
    // MythicLib
    compileOnlyPlugin("io.lumine:MythicLib-dist:1.6-SNAPSHOT")
    // MythicMobs 4
    compileOnlyPlugin("io.lumine.xikage:MythicMobs:4.12.0")
    // MythicMobs 5
    compileOnlyPlugin("io.lumine:Mythic-Dist:5.2.0")
    // NuVotifier
    compileOnlyPlugin("com.vexsoftware:NuVotifier:2.7.3")
    // Oraxen
    compileOnlyPlugin("io.th0rgal:oraxen:1.175.0")
    // PlaceholderAPI
    compileOnlyPlugin("me.clip:placeholderapi:2.11.6")
    // PlayerPoints
    compileOnlyPlugin("org.black_ixx:playerpoints:3.2.5")
    // SCore
    compileOnlyPlugin("com.github.Ssomar-Developement:SCore:3.4.7")
    // ShopGUIPlus
    compileOnlyPlugin("com.github.brcdev-minecraft:shopgui-api:3.0.0")
    // Slimefun4
    compileOnlyPlugin("com.github.Slimefun:Slimefun4:RC-37")
    // SuperiorSkyblock2
    compileOnlyPlugin("com.bgsoftware:SuperiorSkyblockAPI:2022.9")
    // uSkyBlock TODO fix whenever repo is up
    //compileOnlyPlugin("ovh.uskyblock:uSkyBlock-API:2.8.9")
    // Vault
    compileOnlyPlugin("com.github.MilkBowl:VaultAPI:1.7.1")
    // VotingPlugin
    compileOnlyPlugin("com.bencodez:votingplugin:6.15")
    // WildStacker
    compileOnlyPlugin("com.bgsoftware:WildStackerAPI:2023.3")
    // ZNPCsPlus
    compileOnlyPlugin("lol.pyr:znpcsplus-api:2.0.0-SNAPSHOT")
    // BedWars1058
    compileOnlyPlugin("com.andrei1058.bedwars:bedwars-api:24.9")

    // IridiumSkyblock, PyroFishingPro, uSkyBlock
    compileOnlyLibs("libs", listOf("*.jar"))

    // bStats
    implementation("org.bstats:bstats-bukkit-lite:1.8")
    // HikariCP
    implementation("com.zaxxer:HikariCP:5.1.0")
    // slf4j
    implementation("org.slf4j:slf4j-nop:1.7.36")
    // hppc
    implementation("com.carrotsearch:hppc:0.10.0")
    // bungeecord-chat
    implementation("net.md-5:bungeecord-chat:1.20-R0.2") { isTransitive = false }
}

tasks.shadowJar {
    exclude("mojang-translations/*")

    relocate("org.bstats", "com.leonardobishop.quests.libs.bstats")
    relocate("com.zaxxer.hikari", "com.leonardobishop.quests.libs.hikari")
    relocate("org.slf4j", "com.leonardobishop.quests.libs.slf4j")
    relocate("com.carrotsearch.hppc", "com.leonardobishop.quests.libs.hppc")
    relocate("net.md_5.bungee", "com.leonardobishop.quests.libs.bungee")

    minimize {
        exclude(dependency("org.bstats:.*:.*"))
        exclude(dependency("com.zaxxer:.*:.*"))
        exclude(dependency("org.slf4j:.*:.*"))
    }

    archiveClassifier.set(null as String?)
}

// Moved it here for readability reasons
fun DependencyHandler.compileOnlyProject(s: String): Dependency? {
    return compileOnly(project(s))
}

// We want to exclude some libraries not available in 1.8 from server dependencies
fun DependencyHandler.compileOnlyServer(s: String): Dependency {
    return compileOnly(s) {
        exclude(group = "it.unimi.dsi", module = "fastutil")
        exclude(group = "org.apache.maven", module = "maven-resolver-provider")
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }
}

// We don't want compile-only plugin dependencies to be transitive by default
fun DependencyHandler.compileOnlyPlugin(s: String): Dependency {
    return compileOnly(s) {
        isTransitive = false
    }
}

// Another method made just for readability reasons
fun DependencyHandler.compileOnlyLibs(dir: String, include: List<String>): Dependency? {
    return compileOnly(fileTree(mapOf(
        "dir" to dir,
        "include" to include
    )))
}
