description = "AltarPlanner creates altar server schedules. This project is the multi-project parent."

allprojects {
    group = "org.altarplanner"
    version = "1.0-SNAPSHOT"
}

subprojects {
    apply<JavaBasePlugin>()

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_10
        targetCompatibility = JavaVersion.VERSION_1_10
    }

    repositories {
        jcenter()
    }

    dependencyLocking {
        lockAllConfigurations()
    }
}
