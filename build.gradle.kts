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
    // Apply Kotlin only to common and bukkit modules
    if (name != "forge") {
        apply(plugin = "org.jetbrains.kotlin.jvm")

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions.jvmTarget = "17"
        }
    }

    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
