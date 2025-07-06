plugins {
    `java-library`
}

java {
    toolchain {
        // match whatever you’re using in your gateway/designer
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://nexus.inductiveautomation.com/repository/inductiveautomation-releases")
    }
    // if you ever need perspective, etc. you can add the other IA repos here too:
    // maven { url = uri("https://nexus.inductiveautomation.com/repository/inductiveautomation-thirdparty") }
    // maven { url = uri("https://nexus.inductiveautomation.com/repository/inductiveautomation-snapshots") }
}

dependencies {
    // correct groupId/artifactId for ignition-common in 8.1.x:
    compileOnly("com.inductiveautomation.ignitionsdk:ignition-common:8.1.44")
}

sourceSets {
    main {
        resources {
            srcDir("src/main/resources")
            include("**/*.properties")
        }
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets {
    main {
        resources {
            srcDir("src/main/resources")
            include("**/*.properties")
        }
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
