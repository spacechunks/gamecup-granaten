import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    alias(libs.plugins.shadow)
}

group = "space.chunks"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject.libs.minestom)
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
        attributes["Main-Class"] = "space.chunks.gamecup.template.minestom.GameCupExampleMinestomServer"
    }
}