import nu.studer.gradle.jooq.JooqEdition
import nu.studer.gradle.jooq.JooqGenerate
import org.flywaydb.gradle.task.FlywayMigrateTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.Logging

plugins {
    id("org.springframework.boot") version "2.4.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.flywaydb.flyway") version "7.7.0"
    id("nu.studer.jooq") version "5.2"
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.spring") version "1.4.30"
}

group = "com.goodchoice"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val dbDriver = "org.postgresql.Driver"

val dbUrl = System.getenv("JDBC_DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/good-choice"
val dbUser = System.getenv("JDBC_DATABASE_USERNAME") ?: "good-choice-user"
val dbPassword = System.getenv("JDBC_DATABASE_PASSWORD") ?: "good-choice-pass"

val dbTestUrl = "jdbc:postgresql://localhost:5432/good-choice-test"
val dbTestUser = "good-choice-user"
val dbTestPassword = "good-choice-pass"

repositories {
    mavenCentral()
}

val springdocVersion = "1.5.6"
val jooqVersion = dependencyManagement.importedProperties["jooq.version"]

dependencies {
    jooqGenerator("org.postgresql:postgresql")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq-kotlin:$jooqVersion")
    implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-security:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

flyway {
    driver = dbDriver
    url = dbUrl
    user = dbUser
    password = dbPassword
}

jooq {
    version.set(jooqVersion)
    edition.set(JooqEdition.OSS)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)

            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = dbDriver
                    url = dbUrl
                    user = dbUser
                    password = dbPassword
                }

                generator.apply {
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = false
                        isNonnullAnnotation = true
                        nonnullAnnotationType = "org.jetbrains.annotations.NotNull"
                        isNullableAnnotation = true
                        nullableAnnotationType = "org.jetbrains.annotations.Nullable"
                    }

                    target.apply {
                        packageName = "com.goodchoice.domain.common.jooq"
                        directory = "build/generated/sources/jooq/main"
                    }
                }
            }
        }
    }
}

tasks.withType<JooqGenerate> {
    dependsOn(tasks.named<FlywayMigrateTask>("flywayMigrate"))

    inputs.files(fileTree("src/main/resources/db/migration"))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)

    allInputsDeclared.set(true)
    outputs.cacheIf { true }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
        allWarningsAsErrors = true
    }
}

task<FlywayMigrateTask>("flywayMigrateTest") {
    driver = dbDriver
    url = dbTestUrl
    user = dbTestUser
    password = dbTestPassword
}

tasks.withType<Test> {
    dependsOn(tasks.named<FlywayMigrateTask>("flywayMigrateTest"))

    useJUnitPlatform()
}
