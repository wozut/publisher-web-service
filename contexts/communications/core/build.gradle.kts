plugins {
    id("kotlin-library-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT apply false
    id("io.spring.dependency-management") version Versions.SPRING_DEPENDENCY_MANAGEMENT
    kotlin("plugin.spring") version Versions.KOTLIN
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

base {
    archivesName.set("communications.core")
}


dependencies {
    implementation("org.springframework:spring-beans")
    implementation("jakarta.inject:jakarta.inject-api")
    implementation(project(":libraries:ok-http"))
    implementation("com.google.code.gson:gson")
    implementation(project(":libraries:jsonserialization"))
    testImplementation("com.google.code.gson:gson")
}
