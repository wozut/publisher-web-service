plugins {
    id("kotlin-library-conventions")
    id("org.springframework.boot") version "3.5.4" apply false
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "2.0.21"
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
