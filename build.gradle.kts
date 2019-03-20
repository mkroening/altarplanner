description = "AltarPlanner creates altar server schedules. This project is the multi-project parent."

plugins {
    java
    id("org.javamodularity.moduleplugin") version "1.4.1" apply false
    id("com.github.spotbugs") version "1.7.1" apply false
    id("com.github.ben-manes.versions") version "0.21.0"
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
    tasks.withType<Test> {
        useJUnitPlatform()
    }
    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.4.1"))
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }

    apply(plugin = "org.javamodularity.moduleplugin")

    apply(plugin = "checkstyle")
    val checkstyleVersion = "8.18"
    configure<CheckstyleExtension> {
        toolVersion = checkstyleVersion
        isIgnoreFailures = true
    }

    apply(plugin = "com.github.spotbugs")
    val spotbugsVersion = "3.1.12"
    configure<com.github.spotbugs.SpotBugsExtension> {
        toolVersion = spotbugsVersion
        isIgnoreFailures = true
    }

    apply(plugin = "pmd")
    val pmdVersion = "6.12.0"
    configure<PmdExtension> {
        toolVersion = pmdVersion
        ruleSets = listOf()
        ruleSetFiles = files("${project.rootDir}/config/pmd/ruleSet.xml")
        isIgnoreFailures = true
    }

    dependencies {
        default("com.puppycrawl.tools:checkstyle:$checkstyleVersion")
        default("com.github.spotbugs:spotbugs:$spotbugsVersion")
        default("net.sourceforge.pmd:pmd:$pmdVersion")
    }

    repositories {
        mavenCentral()
    }
}

tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
    resolutionStrategy {
        componentSelection {
            all {
                val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea")
                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-+]*") }
                        .any { it.matches(candidate.version) }
                if (rejected) {
                    reject("Release candidate")
                }
            }
        }
    }
}
