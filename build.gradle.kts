import java.net.URL

plugins {
    id("com.github.ben-manes.versions") version "0.39.0"
    id("org.openstreetmap.josm") version "0.8.0"
    id("java")
    id("java-library")
}

group = "org.openstreetmap.josm.plugins.devseed.JosmMagicWand"
version = "3.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
// Repositories Configuration
repositories {
    mavenLocal()
    mavenCentral()
}

// Source Sets Configuration
sourceSets {
    main {
        java {
            srcDir("src")
            include("org/openstreetmap/**")
        }
        resources {
            srcDir(project.projectDir).include("images/**")
        }
    }
}

configurations {
    create("externalLibs")
}

dependencies {
    // Libraries to be packed into the JAR
    packIntoJar("org.locationtech.jts:jts-core:1.19.0")
    packIntoJar("org.locationtech.jts.io:jts-io-common:1.19.0")
    packIntoJar("org.openpnp:opencv:4.7.0-0")
    packIntoJar("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    packIntoJar("com.squareup.okhttp3:okhttp:4.10.0")
}

josm {
    pluginName = "josm_magic_wand"
    debugPort = 1729
    josmCompileVersion = "19230"
    manifest {
        description = "JOSM plugin for select areas by color range."
        mainClass = "org.openstreetmap.josm.plugins.devseed.JosmMagicWand.MainJosmMagicWandPlugin"
        minJosmVersion = "19067"
        author = "yunica"
        canLoadAtRuntime = true
        iconPath = "images/dialogs/magicwand.svg"
        website = URL("https://github.com/developmentseed/JosmMagicWand")
        minJavaVersion = 17
    }
}
