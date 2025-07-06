plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly("com.inductiveautomation.ignitionsdk:gateway-api:8.1.44")
    compileOnly("com.inductiveautomation.ignitionsdk:perspective-gateway:8.1.44")
    implementation(project(":common"))
}
