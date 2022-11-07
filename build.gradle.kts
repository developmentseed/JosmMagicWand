import java.net.URL

plugins {
    id("com.github.ben-manes.versions") version "0.39.0"
    id("org.openstreetmap.josm") version "0.8.0"
    id("java")
    id("java-library")

}

group = "org.openstreetmap.josm.plugins.devseed.JosmMagicWand"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

allprojects {
    repositories {
        mavenLocal()
    }
}
configurations { create("externalLibs") }

repositories {
    mavenCentral()
}

sourceSets {
    create("libs") {
        java {
            srcDir("src").include(listOf("org/openstreetmap/**"))
        }
    }
    main {
        java {
            srcDir("src").include("org/openstreetmap/**")
        }
        resources {
            srcDir(project.projectDir).include("images/**")
        }
    }
}
val libsImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

dependencies {
    packIntoJar("org.locationtech.jts:jts-core:1.19.0")
    packIntoJar("org.openpnp:opencv:4.5.5-1")
    libsImplementation("org.locationtech.jts:jts-core:1.19.0")
    libsImplementation("org.openpnp:opencv:4.5.5-1")
}


josm {
    pluginName = "josm_magic_wand"
    debugPort = 1729
    josmCompileVersion = "18193"
    manifest {
        description = "JOSM plugin for select areas by color range."
        mainClass = "org.openstreetmap.josm.plugins.devseed.JosmMagicWand.MainJosmMagicWandPlugin"
        minJosmVersion = "18193"
        author = "yunica"
        canLoadAtRuntime = true
        iconPath = "images/dialogs/magicwand.svg"
        website = URL("https://github.com/developmentseed/JosmMagicWand")
        minJavaVersion = 11
    }

}