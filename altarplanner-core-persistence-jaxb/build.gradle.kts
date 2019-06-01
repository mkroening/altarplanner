description = "This module contains the an API to marshal and unmarshal the altarplanner-core classes"

plugins {
    `java-library`
}

dependencies {
    testImplementation(project(":altarplanner-core-planning")) {
        because("these are the classes to enable persistence for")
    }

    testImplementation(platform("org.optaplanner:optaplanner-bom:7.19.0.Final"))

    testImplementation("org.optaplanner:optaplanner-core") {
        because("this module directly uses optaplanner to generate sample instances to test marshalling and unmarshalling")
    }

    testImplementation("ch.qos.logback:logback-classic:1.2.3") {
        because("we use this SLF4J API implementation for logging while testing")
    }
}
