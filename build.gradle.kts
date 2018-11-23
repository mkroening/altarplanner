description = "AltarPlanner creates altar server schedules. This project is the multi-project parent."

allprojects {
    group = "org.altarplanner"
    version = "1.0-SNAPSHOT"
}

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("gradle.plugin.com.github.spotbugs:spotbugs-gradle-plugin:1.6.5")
    }
}

subprojects {
    apply(plugin = "java-base")
    apply(from = "${project.rootDir}/staticCodeAnalysis.gradle.kts")

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    dependencyLocking {
        lockAllConfigurations()
    }

    repositories {
        jcenter()
    }
}
