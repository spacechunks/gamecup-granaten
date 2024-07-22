import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.lombok)
}

group = "space.chunks"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject.libs.minestom)
    implementation(rootProject.libs.guice)
    implementation(rootProject.libs.guice.assistedinject)
    implementation(rootProject.libs.jackson.core)
    implementation(rootProject.libs.jackson.databind)
    implementation(rootProject.libs.log4j2)
    implementation(rootProject.libs.log4j2.simple)
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.named("shadowJar", ShadowJar::class) {
    mergeServiceFiles()
    archiveFileName.set("${project.name}.jar")
    manifest {
        attributes["Main-Class"] = "space.chunks.gamecup.dgr.launcher.GameLauncher"
    }
}