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
    val checkstyleVersion: String by project
    toolVersion = checkstyleVersion
    isIgnoreFailures = true
}

apply(plugin = "com.github.spotbugs")
configure<SpotBugsExtension> {
    val spotBugsVersion: String by project
    toolVersion = spotBugsVersion
    isIgnoreFailures = true
}

apply(plugin = "pmd")
configure<PmdExtension> {
    val pmdVersion: String by project
    toolVersion = pmdVersion
    ruleSets = listOf()
    ruleSetFiles = files("${project.rootDir}/config/pmd/ruleSet.xml")
    isIgnoreFailures = true
}
