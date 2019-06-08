description = "This project contains the domain specific implementation of the problem, the integration with OptaPlanner and the IO."

plugins {
    `java-library`
}

dependencies {
    api("org.threeten:threeten-extra:1.5.0") {
        because("we use additional date-time classes like LocalDateRange")
    }

    api(platform("org.optaplanner:optaplanner-bom:7.21.0.Final")) {
        because("we depend on optaplanner projects")
    }

    api("org.optaplanner:optaplanner-core") {
        because("we require a constraint solver, we use HardSoftScore in our API")
    }

    implementation("org.slf4j:slf4j-api:1.7.26") {
        because("we do logging via these interfaces")
    }

    testImplementation("ch.qos.logback:logback-classic:1.2.3") {
        because("we use this SLF4J API implementation for logging while testing")
    }

    testImplementation("org.optaplanner:optaplanner-test") {
        because("we use this JUnit integration to test score rules in DRL")
    }
}
