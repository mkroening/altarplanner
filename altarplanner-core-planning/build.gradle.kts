description = "This project contains the domain specific implementation of the problem, the integration with OptaPlanner and the IO."

plugins {
    `java-library`
}

configurations {
    implementation {
        // Outdated optaplanner-benchmark dependencies, split package between jaxb.impl and jaxb.core
        exclude(group = "com.sun.xml.bind", module = "jaxb-core")
        exclude(group = "com.sun.xml.bind", module = "jaxb-impl")

        // Outdated optaplanner-benchmark dependency, split package between activation and jakarta.activation
        exclude(group = "javax.activation", module = "activation")

        // Unnecessary optaplanner-benchmark dependency
        exclude(group = "org.jboss.spec.javax.xml.bind", module = "jboss-jaxb-api_2.3_spec")
    }
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

    implementation("org.apache.poi:poi-ooxml:4.1.0") {
        because("we use POI-XSSF to write Excel files")
    }

    testImplementation("ch.qos.logback:logback-classic:1.2.3") {
        because("we use this SLF4J API implementation for logging while testing")
    }

    testImplementation("org.optaplanner:optaplanner-benchmark") {
        because("this is used for finding the best algorithm for our domain")
    }

    testImplementation(enforcedPlatform("org.glassfish.jaxb:jaxb-bom:2.3.2")) {
        because("we need to replace the outdated optaplanner-benchmark dependencies")
    }

    testImplementation("jakarta.xml.bind:jakarta.xml.bind-api") {
        because("optaplanner-benchmark uses the JAXB API")
    }

    testRuntime("org.glassfish.jaxb:jaxb-runtime") {
        because("we let optaplanner-benchmark use the JAXB RI")
    }

    testImplementation("org.freemarker:freemarker:2.3.28") {
        because("currently the version provided by optaplanner-benchmark can't be resolved, required for benchmark templates")
    }

    testImplementation("org.optaplanner:optaplanner-test") {
        because("we use this JUnit integration to test score rules in DRL")
    }
}
