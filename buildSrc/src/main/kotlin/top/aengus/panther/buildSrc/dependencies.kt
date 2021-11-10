package top.aengus.panther.buildSrc

object Libs {

    object Spring {
        private const val version = "2.5.4"
        const val thymeleaf = "org.springframework.boot:spring-boot-starter-thymeleaf:$version"
        const val jdbc = "org.springframework.boot:spring-boot-starter-jdbc:$version"
        const val web = "org.springframework.boot:spring-boot-starter-web:$version"
        const val jpa = "org.springframework.boot:spring-boot-starter-data-jpa:$version"
        const val validation = "org.springframework.boot:spring-boot-starter-validation:$version"
        const val devtools = "org.springframework.boot:spring-boot-devtools:$version"
        const val processor = "org.springframework.boot:spring-boot-configuration-processor:$version"
    }

    object Apache {
        const val commonText = "org.apache.commons:commons-text:1.9"
    }

    const val lombok = "org.projectlombok:lombok:1.18.8"
    const val hutool = "cn.hutool:hutool-all:5.7.0"
    const val mysql = "mysql:mysql-connector-java:8.0.16"
    const val xmlBind = "javax.xml.bind:jaxb-api:2.3.0"
    const val jwt = "io.jsonwebtoken:jjwt:0.9.0"
}