description = "AltarPlanner creates altar server schedules. This project is the multi-project parent."

allprojects {
    group = "org.altarplanner"
    version = "1.0-SNAPSHOT"
}

subprojects {
    apply<JavaBasePlugin>()

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<JavaCompile>().configureEach{
        options.encoding = "UTF-8"
    }

    repositories {
        jcenter()
    }

    dependencyLocking {
        lockAllConfigurations()
    }
}
