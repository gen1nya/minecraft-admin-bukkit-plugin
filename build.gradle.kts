plugins {
    kotlin("jvm") version "2.1.0" apply false
}

allprojects {
    group = "online.ebatel"
    version = "1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    // Fabric modules (`:fabric:*`) apply Loom + Kotlin and pick their own Java/Kotlin target
    // (1.20.1 -> 17, 1.21.1 -> 21), so leave them out of the shared 17 defaults below.
    val isFabric = path.startsWith(":fabric")

    // Apply Kotlin only to common and bukkit modules (forge is plain Java).
    if (name != "forge" && !isFabric) {
        apply(plugin = "org.jetbrains.kotlin.jvm")

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions.jvmTarget = "17"
        }
    }

    if (!isFabric) {
        pluginManager.withPlugin("java") {
            configure<JavaPluginExtension> {
                toolchain.languageVersion.set(JavaLanguageVersion.of(17))
            }
        }
    }
}
