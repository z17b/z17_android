pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repo.eclipse.org/content/repositories/paho-releases/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repo.eclipse.org/content/repositories/paho-releases/")
    }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

include(":app")
include(":views")
include(":singledi")
include(":preferences")
include(":compress")
