plugins {
    id("java")
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("plugin.jpa") version "1.9.24"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "back.ecommerce"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://jitpack.io")
    }
}

dependencyManagement {
    imports {
        mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:2.4.4")
    }
}

val asciidoctorExt = "asciidoctorExt"
configurations.create(asciidoctorExt) {
    extendsFrom(configurations.testImplementation.get())
}

val kotestVersion = "5.7.2"
val kotestExtensionSpringVersion = "1.1.3"
val snippets = file("build/generated-snippets")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")


    // Logback Appender Discord
    implementation("org.slf4j:slf4j-api:2.0.0")
    implementation("com.github.napstr:logback-discord-appender:1.0.0")
    implementation("net.logstash.logback:logstash-logback-encoder:7.3")

    //Auth JWT
    implementation("org.springframework.security:spring-security-crypto")
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("javax.xml.bind:jaxb-api:2.3.1")

    //queryDSL
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")


    //AOP
    implementation("org.springframework.boot:spring-boot-starter-aop")

    //AWS SQS
    implementation("io.awspring.cloud:spring-cloud-starter-aws-messaging")
    //AWS S3
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")

    //Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    //spring docs
    implementation("org.springdoc:springdoc-openapi-ui:1.6.15")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")

    //webflux(REST API Client)
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("io.projectreactor:reactor-test")

    //Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //kotest
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:$kotestExtensionSpringVersion")

    // mockk
    testImplementation("io.mockk:mockk:1.13.8")

    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    runtimeOnly("com.mysql:mysql-connector-j")
    testRuntimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    //ULID
    implementation("com.github.f4b6a3:ulid-creator:5.2.3")


}

tasks.test {
    useJUnitPlatform()
    outputs.dir(snippets)
}


tasks.asciidoctor {
    inputs.dir(snippets)
    dependsOn(tasks.test)
    configurations(asciidoctorExt)
}

val register = tasks.register("copyHTML", Copy::class) {
    dependsOn(tasks.asciidoctor)
    from(file("build/docs/asciidoc"))
    into(file("src/main/resources/static/docs"))

    doFirst {
        delete {
            file("src/main/resources/static/docs")
        }
    }
}

tasks.build {
    dependsOn(register)
}
tasks.bootJar {
    dependsOn(register)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}