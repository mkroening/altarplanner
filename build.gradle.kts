import com.github.spotbugs.SpotBugsExtension
import org.javamodularity.moduleplugin.tasks.PatchModuleExtension

description = "AltarPlanner creates altar server schedules. This project is the multi-project parent."

plugins {
    java
    id("org.javamodularity.moduleplugin") version "1.5.0" apply false
    id("com.github.spotbugs") version "3.0.0" apply false
    id("com.github.ben-manes.versions") version "0.27.0"
}

allprojects {
    group = "org.altarplanner"
    version = "0.1-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")
    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_12
        targetCompatibility = JavaVersion.VERSION_12
    }
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
    tasks.withType<Test> {
        useJUnitPlatform()
    }
    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.5.2"))
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    apply(plugin = "org.javamodularity.moduleplugin")
    configure<PatchModuleExtension> {
        config = listOf(
                "xmlpull=xpp3_min-1.1.4c.jar"
        )
    }

    apply(plugin = "checkstyle")
    val checkstyleVersion = "8.28"
    configure<CheckstyleExtension> {
        toolVersion = checkstyleVersion
        isIgnoreFailures = true
    }

    apply(plugin = "com.github.spotbugs")
    val spotbugsVersion = "3.1.12"
    configure<SpotBugsExtension> {
        toolVersion = spotbugsVersion
        isIgnoreFailures = true
    }

    apply(plugin = "pmd")
    val pmdVersion = "6.20.0"
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
                val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea", "0.t0")
                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-+]*") }
                        .any { it.matches(candidate.version) }
                if (rejected) {
                    reject("Release candidate")
                }
            }
        }
    }
}
