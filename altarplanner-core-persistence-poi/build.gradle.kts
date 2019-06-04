description = "This module contains an API to import and export from/to Microsoft Excel files"

plugins {
    `java-library`
}

dependencies {
    implementation(project(":altarplanner-core-planning")) {
        because("these are the classes to import/export")
    }

    implementation("org.apache.poi:poi-ooxml:4.1.0") {
        because("we use POI-XSSF to read/write Excel files")
    }
}