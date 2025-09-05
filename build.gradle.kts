buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

tasks.register("setupGitHooks", Exec::class) {
    description = "Setup Git hooks for all team members"
    group = "setup"
    commandLine("./scripts/setup-git-hooks.sh")
    onlyIf { System.getenv("CI") == null }
}

tasks.named("build") {
    dependsOn("setupGitHooks")
}
