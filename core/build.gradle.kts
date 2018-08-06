description = "This project contains the domain specific implementation of the problem, the integration with OptaPlanner and the IO."

plugins {
    id("java-library")
}

dependencies {
    implementation("org.optaplanner:optaplanner-bom:+") {
        because("we depend on optaplanner projects")
    }

    implementation("org.junit:junit-bom:+") {
        because("we use JUnit modules")
    }

    implementation("org.optaplanner:optaplanner-core") {
        because("we require a constraint solver")
    }

    implementation("org.slf4j:slf4j-api:+") {
        because("we do logging via these interfaces")
    }

    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.0.1") {
        because("we use JAXB-RI for XML Binding")
    }

    implementation("com.migesok:jaxb-java-time-adapters:+") {
        because("these JAXB adapters for JSR-310 save us some boilerplate code")
    }

    implementation("org.optaplanner:optaplanner-persistence-jaxb") {
        because("this helps us serialize optaplanner scores")
    }

    implementation("org.apache.poi:poi-ooxml:+") {
        because("we use POI-XSSF to write Excel files")
    }

    testImplementation("ch.qos.logback:logback-classic:+") {
        because("we use this SLF4J API implementation for logging while testing")
    }

    testImplementation("org.optaplanner:optaplanner-benchmark") {
        because("this is used for finding the best algorithm for our domain")
    }

    testImplementation("org.freemarker:freemarker:2.3.28") {
        because("currently the version provided by optaplanner-benchmark can\"t be resolved, required for benchmark templates")
    }

    testImplementation("org.optaplanner:optaplanner-test") {
        because("we use this JUnit integration to test score rules in DRL")
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api") {
        because("we require a testing API for writing tests")
    }

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine") {
        because("a test engine implementation is required for running the tests")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
