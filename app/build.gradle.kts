description = "This module contains the GUI written in JavaFX with FXML."

plugins {
    application
}

application {
    mainClassName = "org.altarplanner.app.Launcher"
}

val currentOS by extra { org.gradle.internal.os.OperatingSystem.current()!! }
val platform by extra {
    when {
        currentOS.isWindows -> "win"
        currentOS.isLinux -> "linux"
        currentOS.isMacOsX -> "mac"
        else -> ""
    }
}

fun addToModulePath(file: File) = file.name.startsWith("javafx-") || file.name.startsWith("controlsfx")

fun javaArgs(classpath: FileCollection) = listOf(
        "--module-path", classpath.filter { addToModulePath(it) }.asPath,
        "--add-modules", "javafx.controls",
        "--add-modules", "javafx.fxml",
        "--add-modules", "controlsfx",
        "--add-exports", "javafx.base/com.sun.javafx.runtime=controlsfx",
        "--add-exports", "javafx.base/com.sun.javafx.collections=controlsfx",
        "--add-exports", "javafx.controls/com.sun.javafx.scene.control=controlsfx"
)

tasks {
    named<JavaCompile>("compileJava") {
        doFirst {
            options.compilerArgs = javaArgs(classpath)
            classpath = classpath.filter { !addToModulePath(it) }
        }
    }

    named<JavaExec>("run") {
        doFirst {
            jvmArgs = javaArgs(classpath)
            classpath = classpath.filter { !addToModulePath(it) }
        }
    }
}

dependencies {
    compile(project(":core")) {
        because("we need the domain, solver and IO implementation of the core project")
    }

    val javafxVersion: String by project
    compile("org.openjfx:javafx-base:$javafxVersion:$platform") {
        because("javafx.graphics depends on javafx.base")
    }

    compile("org.openjfx:javafx-graphics:$javafxVersion:$platform") {
        because("javafx.controls depends on javafx.graphics")
    }

    compile("org.openjfx:javafx-controls:$javafxVersion:$platform") {
        because("we use the JavaFX framework and its controls")
    }

    compile("org.openjfx:javafx-fxml:$javafxVersion:$platform") {
        because("we define the user interface via FXML")
    }

    val logbackVersion: String by project
    compile("ch.qos.logback:logback-classic:$logbackVersion") {
        because("we require a SLF4J binding for logging")
    }

    compile("org.controlsfx:controlsfx:9.0.0") {
        because("we use the CheckComboBox")
    }
}
