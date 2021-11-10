import top.aengus.panther.buildSrc.Libs

plugins {
    id("java")
    id("org.springframework.boot") version "2.5.4"
    id("org.flywaydb.flyway") version "8.0.3"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

group = "top.aengus.panther"
version = "0.0.1"
description = "panther"
java.sourceCompatibility = JavaVersion.VERSION_1_8

springBoot {
    buildInfo()
}

flyway {
    url = "jdbc:mysql://127.0.0.1:3306/panther?serverTimezone=GMT%2B8"
    user = "pantherAdmin"
    password = "pantherJF=A77922"
}

dependencies {
    annotationProcessor(Libs.lombok)
    annotationProcessor(Libs.Spring.processor)

    implementation(Libs.Spring.jdbc)
    implementation(Libs.Spring.thymeleaf)
    implementation(Libs.Spring.web)
    implementation(Libs.Spring.jpa)
    implementation(Libs.Spring.validation)
    developmentOnly(Libs.Spring.devtools)

    implementation(Libs.Apache.commonText)
    implementation(Libs.lombok)
    implementation(Libs.hutool)
    implementation(Libs.jwt)
    implementation(Libs.flyway)
    runtimeOnly(Libs.xmlBind)
    runtimeOnly(Libs.mysql)
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}
