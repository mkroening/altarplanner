description = "This module contains the GUI written in JavaFX with FXML."

plugins {
    application
}

application {
    mainClassName = "org.altarplanner.app.Launcher"
}

val currentOS = org.gradle.internal.os.OperatingSystem.current()!!
val platform = when {
    currentOS.isWindows -> "win"
    currentOS.isLinux -> "linux"
    currentOS.isMacOsX -> "mac"
    else -> ""
}

fun addToModulePath(file: File) = when {
    file.name.startsWith("javafx-")
            || file.name.contains("controlsfx") -> true
    else -> false
}

fun javaArgs(classpath: FileCollection) = listOf(
        "--module-path", classpath.filter{addToModulePath(it)}.asPath,
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
            classpath = classpath.filter{!addToModulePath(it)}
        }
    }

    named<JavaExec>("run") {
        doFirst {
            jvmArgs = javaArgs(classpath)
            classpath = classpath.filter{!addToModulePath(it)}
        }
    }
}

dependencies {
    compile(project(":core")) {
        because("we need the domain, solver and IO implementation of the core project")
    }

    compile("org.openjfx:javafx-base:11:$platform") {
        because("javafx.graphics depends on javafx.base")
    }

    compile("org.openjfx:javafx-graphics:11:$platform") {
        because("javafx.controls depends on javafx.graphics")
    }

    compile("org.openjfx:javafx-controls:11:$platform") {
        because("we use the JavaFX framework and its controls")
    }

    compile("org.openjfx:javafx-fxml:11:$platform") {
        because("we define the user interface via FXML")
    }

    compile("ch.qos.logback:logback-classic:+") {
        because("we require a SLF4J binding for logging")
    }

    compile("org.controlsfx:controlsfx:+") {
        because("we use the CheckComboBox")
    }
}
