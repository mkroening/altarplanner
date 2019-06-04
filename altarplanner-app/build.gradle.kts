description = "This module contains the GUI written in JavaFX with FXML."

plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.7"
}

javafx {
    version = "12.0.1"
    modules = listOf(
            "javafx.controls",
            "javafx.fxml"
    )
}

dependencies {
    compile("org.controlsfx:controlsfx:11.0.0") {
        because("we use the CheckComboBox")
    }
    
    compile(project(":altarplanner-core-planning")) {
        because("we need the domain, solver and IO implementation of the core project")
    }

    compile(project(":altarplanner-core-persistence-jaxb")) {
        because("the data persists in XML files")
    }

    compile(project(":altarplanner-core-persistence-poi")) {
        because("we need to import and export from/to Microsoft Excel files")
    }

    compile("org.slf4j:slf4j-api:1.7.26") {
        because("we do logging via these interfaces")
    }

    runtime("ch.qos.logback:logback-classic:1.2.3") {
        because("we use the logback slf4j implementation")
    }

    runtime("org.glassfish.jaxb:jaxb-runtime") {
        because("we use the JAXB RI")
    }
}

application {
    mainClassName = "$moduleName/org.altarplanner.app.Launcher"
    applicationDefaultJvmArgs = listOf(
            "--add-opens", "java.base/java.lang=org.drools.core",
            "--add-opens", "java.base/java.util=xstream",
            "--add-opens", "java.base/java.lang.reflect=xstream",
            "--add-opens", "java.base/java.text=xstream",
            "--add-opens", "java.desktop/java.awt.font=xstream"
    )
}
