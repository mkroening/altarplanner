description = "This module contains the GUI written in JavaFX with FXML."

plugins {
    id("application")
}

application {
    mainClassName = "org.altarplanner.app.Launcher"
}

dependencies {
    compile(project(":core")) {
        because("we need the domain, solver and IO implementation of the core project")
    }

    compile("ch.qos.logback:logback-classic:+") {
        because("we require a SLF4J binding for logging")
    }

    compile("org.controlsfx:controlsfx:+") {
        because("we use the CheckComboBox")
    }
}
