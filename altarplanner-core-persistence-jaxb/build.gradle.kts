description = "This module contains the an API to marshal and unmarshal the altarplanner-core classes"

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
    api(project(":altarplanner-core-planning")) {
        because("these are the classes to enable persistence for")
    }

    api(enforcedPlatform("org.glassfish.jaxb:jaxb-bom:2.3.2")) {
        because("the JAXB API and runtime should be in sync")
    }

    api("jakarta.xml.bind:jakarta.xml.bind-api") {
        because("we use the JAXB API for XML Binding")
    }

    implementation(platform("org.optaplanner:optaplanner-bom:7.21.0.Final"))

    implementation("org.optaplanner:optaplanner-core") {
        because("this module directly uses optaplanner to generate sample instances to test marshalling and unmarshalling")
    }

    implementation("org.optaplanner:optaplanner-persistence-jaxb") {
        because("we need an XmlAdapter for HardSoftScore")
    }

    implementation("io.github.threeten-jaxb:threeten-jaxb-core:1.2") {
        because("we need XmlAdapters for the ThreeTen date and time API")
    }

    testRuntime("ch.qos.logback:logback-classic:1.2.3") {
        because("we use this SLF4J API implementation for logging while testing")
    }

    testRuntime("org.glassfish.jaxb:jaxb-runtime") {
        because("we use the RI JAXB runtime for testing")
    }
}
