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
    archivesName.set("accounts.core")
}

dependencies {
    implementation("jakarta.inject:jakarta.inject-api")
    implementation(project(":libraries:transactional"))
    implementation(project(":libraries:logging"))
    implementation(project(":contexts:authentication:core"))

    testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testFixturesImplementation(platform("io.arrow-kt:arrow-stack:2.1.2"))
    testFixturesImplementation("io.arrow-kt:arrow-core")
}
