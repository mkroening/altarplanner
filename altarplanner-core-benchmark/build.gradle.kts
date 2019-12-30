description = "This module is an app for benchmarking the planning performance"

plugins {
    application
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
    implementation(project(":altarplanner-core-planning")) {
        because("this is the planning library to benchmark")
    }

    implementation(project(":altarplanner-core-persistence-jaxb")) {
        because("we need to dynamically load different schedules")
    }

    implementation("org.optaplanner:optaplanner-benchmark") {
        because("this is used for finding the best algorithm for our domain")
    }

    implementation("org.freemarker:freemarker:2.3.29") {
        because("currently the version provided by optaplanner-benchmark can't be resolved, required for benchmark templates")
    }

    implementation("org.slf4j:slf4j-api:1.7.26") {
        because("we do logging via these interfaces")
    }

    runtime("org.glassfish.jaxb:jaxb-runtime") {
        because("we use the JAXB RI")
    }

    runtime("ch.qos.logback:logback-classic:1.2.3") {
        because("we use this SLF4J API implementation for logging while testing")
    }
}

application {
    mainClassName = "$moduleName/org.altarplanner.core.benchmark.Launcher"
    applicationDefaultJvmArgs = listOf(
            "--add-modules", "java.scripting",
            "--add-opens", "java.base/java.lang=org.drools.core",
            "--add-opens", "java.base/java.util=xstream",
            "--add-opens", "java.base/java.lang.reflect=xstream",
            "--add-opens", "java.base/java.text=xstream",
            "--add-opens", "java.desktop/java.awt.font=xstream",

            "--add-opens", "org.threeten.extra/org.threeten.extra=xstream",
            "--add-opens", "java.base/java.util.concurrent=xstream",
            "--add-opens", "java.base/java.util.concurrent.locks=xstream",
            "--add-opens", "org.altarplanner.core.planning/org.altarplanner.core.planning.domain=xstream",
            "--add-opens", "org.altarplanner.core.planning/org.altarplanner.core.planning.domain.mass=xstream",
            "--add-opens", "org.altarplanner.core.planning/org.altarplanner.core.planning.domain.planning=xstream",
            "--add-opens", "org.altarplanner.core.planning/org.altarplanner.core.planning.domain.state=xstream"
    )
}
