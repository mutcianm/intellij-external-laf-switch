fun platform(fullVersion: Any) = fullVersion.toString().drop(2).replace(".", "")

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

val pluginVersion = "2024.3"
val branchVersion = platform(pluginVersion)

group = "com.github.mutcianm"
version = pluginVersion

// Configure project's dependencies
repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        snapshots()
        nightly()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("$branchVersion-EAP-SNAPSHOT", useInstaller = false)
    }
}

intellijPlatform.instrumentCode.set(false)

tasks.patchPluginXml {
    pluginName.set("External LAF Switch")
    sinceBuild.set("$branchVersion.0")
    untilBuild.set("$branchVersion.*")
}

tasks.publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
}

tasks.buildSearchableOptions {
    enabled = false
}