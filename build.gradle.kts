import top.aengus.panther.buildSrc.Libs

plugins {
    id("java")
    id("org.springframework.boot") version "2.5.4"
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
    runtimeOnly(Libs.xmlBind)
    runtimeOnly(Libs.mysql)
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}
