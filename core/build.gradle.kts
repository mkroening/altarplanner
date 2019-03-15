description = "This project contains the domain specific implementation of the problem, the integration with OptaPlanner and the IO."

plugins {
    `java-library`
}

patchModules.config = listOf(
        "xmlpull=xpp3_min-1.1.4c.jar"
)

configurations {
    "implementation" {
        exclude(group = "com.sun.xml.bind", module = "jaxb-core")
        exclude(group = "com.sun.xml.bind", module = "jaxb-impl")
        exclude(group = "javax.activation", module = "activation")
        exclude(group = "org.jboss.spec.javax.xml.bind", module = "jboss-jaxb-api_2.3_spec")
    }
}

dependencies {
    api("org.threeten:threeten-extra:1.5.0") {
        because("we use additional date-time classes like LocalDateRange")
    }

    implementation(platform("org.optaplanner:optaplanner-bom:7.18.0.Final")) {
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
    api(enforcedPlatform("org.glassfish.jaxb:jaxb-bom:$jaxbVersion")) {
        because("we depend on official JAXB artifacts")
    }

    api("jakarta.xml.bind:jakarta.xml.bind-api") {
        because("we use the JAXB API for XML Binding")
    }

    implementation("org.glassfish.jaxb:jaxb-runtime") {
        because("we want to use the RI JAXB runtime")
    }

    implementation("com.migesok:jaxb-java-time-adapters:1.1.3") {
        because("these JAXB adapters for JSR-310 save us some boilerplate code")
    }

    implementation("org.optaplanner:optaplanner-persistence-jaxb") {
        because("this helps us serialize optaplanner scores")
    }

    implementation("org.apache.poi:poi-ooxml:4.0.1") {
        because("we use POI-XSSF to write Excel files")
    }

    testImplementation(enforcedPlatform("org.junit:junit-bom:5.4.0")) {
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
