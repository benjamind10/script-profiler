// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        // <— this one aggregates all IA artifacts (module-signer, sdk-plugins, etc)
        maven { url = uri("https://nexus.inductiveautomation.com/repository/public/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenLocal()
        maven { url = uri("https://nexus.inductiveautomation.com/repository/public/") }
        mavenCentral()
        // plus any extra Ivy repos you need…
    }
}
rootProject.name = "script-profiler"
//include( "gateway", "designer")
include("common", "gateway", "designer")
