import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("fabric-loom") version "1.7.4"
}

val minecraftVersion = "1.21.1"
val fabricApiVersion = "0.116.12+1.21.1"
val loaderVersion = "0.16.10"
val targetJava = 21

base {
    // remapped artifact: WebAdminPlugin-Fabric-1.21.1-1.0.jar
    archivesName.set("WebAdminPlugin-Fabric-$minecraftVersion")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    // Same mappings family the Forge module uses, so the shared source compiles unchanged here.
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

    // gson is shipped by Minecraft at runtime; kotlin-stdlib by fabric-language-kotlin.
    compileOnly("com.google.code.gson:gson:2.10.1")
}

// Compile the shared logic and the common data classes straight into this version's jar.
sourceSets {
    main {
        kotlin.srcDir(rootProject.file("fabric/shared/src/main/kotlin"))
        kotlin.srcDir(rootProject.file("common/src/main/kotlin"))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(targetJava)
    targetCompatibility = JavaVersion.toVersion(targetJava)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(targetJava)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = targetJava.toString()
    // Loom 1.7's bundled kotlinx-metadata only understands metadata up to Kotlin 2.0, while the
    // project compiles with Kotlin 2.1. Emitting 2.0 metadata keeps Loom's remapper happy; none of
    // the shared code uses 2.1-only language features.
    kotlinOptions.languageVersion = "2.0"
    kotlinOptions.apiVersion = "2.0"
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to project.version.toString()))
    }
}
