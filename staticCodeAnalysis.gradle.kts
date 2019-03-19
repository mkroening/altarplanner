import com.github.spotbugs.SpotBugsExtension

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("gradle.plugin.com.github.spotbugs:spotbugs-gradle-plugin:1.6.5")
        classpath("org.javamodularity:moduleplugin:1.4.1")
    }
}

apply(plugin = "checkstyle")
configure<CheckstyleExtension> {
    toolVersion = "8.16"
    isIgnoreFailures = true
}

apply(plugin = "com.github.spotbugs")
configure<SpotBugsExtension> {
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
