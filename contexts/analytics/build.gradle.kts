plugins {
    id("kotlin-library-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT apply false
    id("io.spring.dependency-management") version Versions.SPRING_DEPENDENCY_MANAGEMENT
    kotlin("plugin.spring") version Versions.KOTLIN
    kotlin("plugin.jpa") version Versions.KOTLIN
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

base {
    archivesName.set("analytics")
}


dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin"){
        because("required by Spring")
    }
    implementation("org.jetbrains.kotlin:kotlin-reflect"){
        because("required by Spring")
    }

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.google.code.gson:gson")

    implementation(project(":contexts:authentication:core"))
    implementation(project(":libraries:jsonserialization"))
    implementation(project(":libraries:json-api"))
    implementation(project(":libraries:search"))
    implementation(project(":libraries:time"))
    implementation(project(":libraries:uuid-validation"))
    implementation(project(":libraries:transactional"))

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation(testFixtures(project(":contexts:tcla:core")))
    testImplementation(testFixtures(project(":libraries:time")))

    implementation("jakarta.inject:jakarta.inject-api")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
}
