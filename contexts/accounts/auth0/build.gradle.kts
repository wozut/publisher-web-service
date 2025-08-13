plugins {
    id("kotlin-library-conventions")
    id("org.springframework.boot") version "3.5.4" apply false
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "1.9.25"
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin"){
        because("required by Spring")
    }
    implementation("org.jetbrains.kotlin:kotlin-reflect"){
        because("required by Spring")
    }
    implementation("org.springframework:spring-beans")

    implementation("com.auth0:auth0:2.23.0")
    implementation(project(":libraries:ok-http"))
    implementation(project(":libraries:jsonserialization"))
    implementation(project(":contexts:accounts:core"))
    implementation("jakarta.inject:jakarta.inject-api")
}
