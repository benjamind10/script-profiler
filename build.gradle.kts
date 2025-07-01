// build.gradle.kts
plugins {
    base
    id("io.ia.sdk.modl") version "0.1.1"
}

allprojects {
    version = "1.0.0"
    group = "com.shiva"
}

ignitionModule {
    fileName.set("script-profiler")
    name.set("Script Profiler")
    id.set("com.shiva.script-profiler")
//    moduleVersion.set(project.version)
    moduleDescription.set("A module that  profiles script execution.")
    requiredIgnitionVersion.set("8.1.40")
    skipModlSigning.set(true)

    projectScopes.putAll(mapOf(
        ":gateway"  to "G",
        ":designer" to "D"
    ))

    hooks.putAll(mapOf(
        "com.shiva.gateway.ScriptProfilerHook"         to "G",
        "com.shiva.designer.ScriptProfilerDesignerHook" to "D"
    ))
}
