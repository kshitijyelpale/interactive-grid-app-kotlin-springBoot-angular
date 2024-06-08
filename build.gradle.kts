import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.flywaydb.flyway") version "9.8.1"
    id("com.github.node-gradle.node") version "7.0.2"
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
//    id("org.hibernate.orm") version "6.4.4.Final"
    id("org.graalvm.buildtools.native") version "0.9.28"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
}

val springBootVersion = "3.2.5"
val springSecurityVersion = "5.7.10"
val testContainersVersion = "1.16.3"
val postgresqlVersion = "42.5.4"

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone")
}

sourceSets {
    create("integrationTest") {
        resources.srcDirs(file("src/test-integration/resources"), file("src/main/resources"))
        java.srcDir(file("src/test-integration/kotlin"))
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val integrationTestRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.testRuntimeOnly.get())
}

tasks.register("createIntegrationTestDirs") {
    doLast {
        val dirs = listOf(
            "src/test-integration/kotlin",
            "src/test-integration/resources"
        )
        dirs.forEach { dir ->
            val file = file(dir)
            if (!file.exists()) {
                file.mkdirs()
                println("Created directory: $dir")
            }
        }
    }
}

val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")
//    finalizedBy(tasks.jacocoTestReport)
}

tasks.check { dependsOn(integrationTest) }

// Ensure directories are created before compiling integration tests
//tasks.named("compileIntegrationTestKotlin").configure {
//    dependsOn("createIntegrationTestDirs")
//}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-rest:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-hateoas:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-validation:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging:2.1.23")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("io.mockk:mockk:1.12.0")
    integrationTestImplementation("com.ninja-squad:springmockk:3.1.2")
    integrationTestImplementation("org.springframework.security:spring-security-test:$springSecurityVersion")
    integrationTestImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    integrationTestImplementation("org.testcontainers:postgresql:$testContainersVersion")
    integrationTestImplementation("org.testcontainers:testcontainers-bom:$testContainersVersion")
    integrationTestImplementation("org.postgresql:postgresql:$postgresqlVersion")
}

node {
    version.set("20.12.2")
    npmVersion.set("10.5.0")
    download.set(true)
    download.set(System.getProperty("os.arch") != "aarch64")
    workDir.set(file("${project.layout.buildDirectory.get()}/frontend"))
    npmWorkDir.set(file("${project.layout.buildDirectory.get()}/frontend"))
    nodeProjectDir.set(file("${project.projectDir}/frontend"))
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("buildNpm") {
    outputs.cacheIf { true }
    args.set(listOf("run", "build"))
    dependsOn("npm_install", "npm_lint", "npm_test")
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("npm_lint") {
    args.set(listOf("run", "lint"))
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("npm_test") {
    args.set(listOf("run", "test:ci"))
}

tasks.register<Copy>("copyWebApp") {
    dependsOn("buildNpm")
    description = "Copies built project to where it will be served"
    from("${layout.buildDirectory.get()}/frontend-dist")
    into("${layout.buildDirectory.get()}/resources/main/static/.")
    outputs.cacheIf { true }
}

tasks.withType<KotlinCompile> {
    dependsOn("copyWebApp")
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

flyway {
    user = "grid"
    password = "grid"
    url = "jdbc:postgresql://localhost:5432/postgres"
}

//hibernate {
//    enhancement {
//        enableAssociationManagement.set(true)
//    }
//}

