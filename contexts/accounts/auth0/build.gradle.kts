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

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin"){
        because("required by Spring")
    }
    implementation("org.jetbrains.kotlin:kotlin-reflect"){
        because("required by Spring")
    }
    implementation("org.springframework:spring-beans")

    implementation("com.auth0:auth0:${Versions.AUTH0_SDK}")
    implementation(project(":libraries:ok-http"))
    implementation(project(":libraries:jsonserialization"))
    implementation(project(":contexts:accounts:core"))
    implementation("jakarta.inject:jakarta.inject-api")
}
