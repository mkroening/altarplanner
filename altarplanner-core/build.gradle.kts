description = "This project contains the domain specific implementation of the problem, the integration with OptaPlanner and the IO."

plugins {
    `java-library`
}

configurations {
    implementation {
        // Outdated optaplanner-persistence-jaxb dependencies, split package between jaxb.impl and jaxb.core
        exclude(group = "com.sun.xml.bind", module = "jaxb-core")
        exclude(group = "com.sun.xml.bind", module = "jaxb-impl")

        // Outdated optaplanner-persistence-jaxb dependency, split package between activation and jakarta.activation
        exclude(group = "javax.activation", module = "activation")

        // Unnecessary optaplanner-persistence-jaxb dependency
        exclude(group = "org.jboss.spec.javax.xml.bind", module = "jboss-jaxb-api_2.3_spec")
    }
}

dependencies {
    api("org.threeten:threeten-extra:1.5.0") {
        because("we use additional date-time classes like LocalDateRange")
    }

    implementation(platform("org.optaplanner:optaplanner-bom:7.21.0.Final")) {
        because("we depend on optaplanner projects")
    }

    implementation("org.optaplanner:optaplanner-core") {
        because("we require a constraint solver")
    }

    implementation("org.slf4j:slf4j-api:1.7.26") {
        because("we do logging via these interfaces")
    }

    api(enforcedPlatform("org.glassfish.jaxb:jaxb-bom:2.3.2")) {
        because("we depend on official JAXB artifacts")
    }

    api("jakarta.xml.bind:jakarta.xml.bind-api") {
        because("we use the JAXB API for XML Binding")
    }

    implementation("org.glassfish.jaxb:jaxb-runtime") {
        because("we want to use the RI JAXB runtime")
    }

    implementation("io.github.threeten-jaxb:threeten-jaxb-core:1.2") {
        because("these JAXB adapters for JSR-310 save us some boilerplate code")
    }

    implementation("org.optaplanner:optaplanner-persistence-jaxb") {
        because("this helps us serialize optaplanner scores")
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

    testImplementation("org.freemarker:freemarker:2.3.28") {
        because("currently the version provided by optaplanner-benchmark can't be resolved, required for benchmark templates")
    }

    testImplementation("org.optaplanner:optaplanner-test") {
        because("we use this JUnit integration to test score rules in DRL")
    }
}
