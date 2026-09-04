pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven {
            name = "MavenCentralGoogleMirror"
            url = uri("https://maven-central.storage-download.googleapis.com/maven2/")
        }
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven {
            name = "MavenCentralGoogleMirror"
            url = uri("https://maven-central.storage-download.googleapis.com/maven2/")
        }
        mavenCentral()
    }
}

rootProject.name = "eazpire-shop"
include(":app")
include(":android-shared")
// Monorepo: ../android-shared. Mirror sync: ./android-shared (same root).
project(":android-shared").projectDir =
    listOf(file("android-shared"), file("../android-shared")).first { it.isDirectory }
