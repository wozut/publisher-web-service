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


base {
    archivesName.set("real-time-collaboration.web-api.websocket")
}


dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin"){
        because("required by Spring")
    }
    implementation("org.jetbrains.kotlin:kotlin-reflect"){
        because("required by Spring")
    }

    implementation("org.springframework.boot:spring-boot-starter-websocket")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}
