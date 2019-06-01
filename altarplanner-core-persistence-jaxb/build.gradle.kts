description = "This module contains the an API to marshal and unmarshal the altarplanner-core classes"

plugins {
    `java-library`
}

dependencies {
    implementation(project(":altarplanner-core-planning")) {
        because("these are the classes to enable persistence for")
    }

    api(enforcedPlatform("org.glassfish.jaxb:jaxb-bom:2.3.2")) {
        because("the JAXB API and runtime should be in sync")
    }

    api("jakarta.xml.bind:jakarta.xml.bind-api") {
        because("we use the JAXB API for XML Binding")
    }

    implementation(platform("org.optaplanner:optaplanner-bom:7.19.0.Final"))

    implementation("org.optaplanner:optaplanner-core") {
        because("this module directly uses optaplanner to generate sample instances to test marshalling and unmarshalling")
    }

    testRuntime("ch.qos.logback:logback-classic:1.2.3") {
        because("we use this SLF4J API implementation for logging while testing")
    }

    testImplementation("org.glassfish.jaxb:jaxb-runtime") {
        because("we use the RI JAXB runtime for testing")
    }
}
