description = "AltarPlanner creates altar server schedules. This project is the multi-project parent."

plugins {
    id("org.javamodularity.moduleplugin") version "1.4.1" apply false
    id("com.github.spotbugs") version "1.7.1" apply false
}

allprojects {
    group = "org.altarplanner"
    version = "0.1-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")
    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
    
    apply(plugin = "org.javamodularity.moduleplugin")

    apply(plugin = "checkstyle")
    configure<CheckstyleExtension> {
        toolVersion = "8.16"
        isIgnoreFailures = true
    }

    apply(plugin = "com.github.spotbugs")
    configure<com.github.spotbugs.SpotBugsExtension> {
        toolVersion = "3.1.10"
        isIgnoreFailures = true
    }

    apply(plugin = "pmd")
    configure<PmdExtension> {
        toolVersion = "6.10.0"
        ruleSets = listOf()
        ruleSetFiles = files("${project.rootDir}/config/pmd/ruleSet.xml")
        isIgnoreFailures = true
    }

    repositories {
        jcenter()
    }
}
