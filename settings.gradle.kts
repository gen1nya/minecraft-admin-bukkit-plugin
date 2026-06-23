pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net")
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "minecraft-admin-plugin"

include("common")
include("bukkit")
include("forge")

// Fabric: one thin module per Minecraft version. Both pull in the shared logic from
// fabric/shared/src/main/kotlin via kotlin.srcDir (see each module's build script) — that
// directory is NOT a standalone Gradle project because Loom can only target one MC version
// at a time, so the shared source has to be compiled once per version.
include("fabric:mc1_20")
include("fabric:mc1_21")
