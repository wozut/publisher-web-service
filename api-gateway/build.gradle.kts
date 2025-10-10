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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.auth0:java-jwt:${Versions.AUTH0_JWT}")
    implementation("com.auth0:jwks-rsa:${Versions.AUTH0_JWKS_RSA}")
    implementation(project(":contexts:authentication:core"))

    testFixturesImplementation(platform("io.arrow-kt:arrow-stack:${Versions.ARROW}"))
    testFixturesImplementation("io.arrow-kt:arrow-core")
}
