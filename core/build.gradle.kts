description = "This project contains the domain specific implementation of the problem, the integration with OptaPlanner and the IO."

plugins {
    `java-library`
}

dependencies {
    api("org.threeten:threeten-extra:+") {
        because("we use additional date-time classes like LocalDateRange")
    }

    implementation("org.optaplanner:optaplanner-bom:+") {
        because("we depend on optaplanner projects")
    }

    implementation("org.optaplanner:optaplanner-core") {
        because("we require a constraint solver")
    }

    val slf4jVersion: String by project
    implementation("org.slf4j:slf4j-api:$slf4jVersion") {
        because("we do logging via these interfaces")
    }

    val jaxbVersion: String by project
    implementation("javax.xml.bind:jaxb-api:$jaxbVersion") {
        because("we use the JAXB API for XML Binding")
    }

    implementation("org.glassfish.jaxb:jaxb-runtime:$jaxbVersion") {
        because("we want to use the RI JAXB runtime")
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

    testImplementation("org.junit:junit-bom:+") {
        because("we use JUnit modules")
    }

    val logbackVersion: String by project
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion") {
        because("we use this SLF4J API implementation for logging while testing")
    }

    testImplementation("org.optaplanner:optaplanner-benchmark") {
        because("this is used for finding the best algorithm for our domain")
    }

    val freemarkerVersion: String by project
    testImplementation("org.freemarker:freemarker:$freemarkerVersion") {
        because("currently the version provided by optaplanner-benchmark can't be resolved, required for benchmark templates")
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
