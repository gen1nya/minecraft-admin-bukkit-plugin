plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        archiveBaseName.set("WebAdminPlugin-Bukkit")
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}
