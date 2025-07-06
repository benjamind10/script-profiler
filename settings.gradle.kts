pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://nexus.inductiveautomation.com/repository/public/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenLocal()
        maven { url = uri("https://nexus.inductiveautomation.com/repository/public/") }
        mavenCentral()
    }
}
rootProject.name = "script-profiler"
include("common", "gateway", "designer", "web")
