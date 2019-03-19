description = "This module contains the GUI written in JavaFX with FXML."

plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.7"
}

patchModules.config = listOf(
        "xmlpull=xpp3_min-1.1.4c.jar"
)

javafx.modules = listOf(
        "javafx.controls",
        "javafx.fxml"
)

dependencies {
    compile(project(":core")) {
        because("we need the domain, solver and IO implementation of the core project")
    }

    compile("ch.qos.logback:logback-classic:1.2.3") {
        because("we require a SLF4J binding for logging")
    }

    compile("org.controlsfx:controlsfx:9.0.0") {
        because("we use the CheckComboBox")
    }
}

application {
    mainClassName = "$moduleName/org.altarplanner.app.Launcher"
    applicationDefaultJvmArgs = listOf(
            "--add-exports", "javafx.base/com.sun.javafx.runtime=controlsfx",
            "--add-exports", "javafx.base/com.sun.javafx.collections=controlsfx",
            "--add-exports", "javafx.controls/com.sun.javafx.scene.control=controlsfx",
            "--add-opens", "java.base/java.lang=org.drools.core",
            "--add-opens", "java.base/java.util=xstream",
            "--add-opens", "java.base/java.lang.reflect=xstream",
            "--add-opens", "java.base/java.text=xstream",
            "--add-opens", "java.desktop/java.awt.font=xstream"
    )
}
