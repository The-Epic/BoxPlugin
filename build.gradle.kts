plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

group = "me.twostinkysocks"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.miles.sh/libraries")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.bg-software.com/repository/api/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.19.1-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly(fileTree("libs"))
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")
    compileOnly("org.jetbrains:annotations:24.0.0")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:2023.1")
    compileOnly("fr.skytasul:beautyquests-core:0.20.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.lumine:Mythic-Dist:5.3.5")

    implementation("org.quartz-scheduler:quartz:2.3.0")
    implementation("io.github.rapha149.signgui:signgui:2.2")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.8")
    implementation(platform("com.intellectualsites.bom:bom-1.18.x:1.25"))
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveVersion.set("")
    archiveFileName = "BoxPlugins-${project.version}.jar"

    // relocations
    relocate("com.github.stefvanschie.inventoryframework", "me.twostinkysocks.libs.inventoryframework")
}




tasks.register("copyJar", Copy::class) {
    doNotTrackState("")
    val target = System.getenv("plugin-dir")

    if (target != null) {
        from(tasks.shadowJar)
        into(file(target))
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
    finalizedBy(tasks.getByName("copyJar"))
}

bukkit {
    name = "BoxPlugin"
    version = project.version as String
    main = "me.twostinkysocks.BoxPlugin"
    apiVersion = "1.19"
}