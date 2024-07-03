plugins {
    id("java")
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
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
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")


    // Logback Appender Discord
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("com.github.napstr:logback-discord-appender:1.0.0")
    implementation("net.logstash.logback:logstash-logback-encoder:7.3")

    //Auth JWT
    implementation("org.springframework.security:spring-security-crypto")
    implementation("io.jsonwebtoken:jjwt:0.9.1")

    //queryDSL
    implementation("com.querydsl:querydsl-jpa:5.0.0")
    implementation("com.querydsl:querydsl-apt:5.0.0")

    //AOP
    implementation("org.springframework.boot:spring-boot-starter-aop")

    //AWS SQS
    implementation("io.awspring.cloud:spring-cloud-starter-aws-messaging")

    //Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    //spring docs
    implementation("org.springdoc:springdoc-openapi-ui:1.6.15")

    //webflux(REST API Client)
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("io.projectreactor:reactor-test")

    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    runtimeOnly("com.mysql:mysql-connector-j")
    testRuntimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

val querydslDir = file("build/generated/querydsl")

tasks.compileJava {
    options.generatedSourceOutputDirectory.set(querydslDir)
}