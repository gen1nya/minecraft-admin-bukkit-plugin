plugins {
    id("java")
    id("net.minecraftforge.gradle") version "6.0.+"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

minecraft {
    mappings("official", "1.20.1")

    runs {
        create("server") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
        }
    }
}

repositories {
    maven("https://maven.minecraftforge.net")
}

dependencies {
    implementation(project(":common"))
    minecraft("net.minecraftforge:forge:1.20.1-47.4.10")
}

tasks {
    jar {
        archiveBaseName.set("WebAdminPlugin-Forge")
        archiveClassifier.set("")
        archiveVersion.set("")

        // Include common module classes in the jar
        from(project(":common").sourceSets.main.get().output)

        manifest {
            attributes(
                "Specification-Title" to "WebAdminPlugin",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        }
    }
}
