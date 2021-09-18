package top.aengus.panther.buildSrc

object Libs {

    object Spring {
        private const val version = "2.5.4"
        const val THYMELEAF = "org.springframework.boot:spring-boot-starter-thymeleaf:$version"
        const val JDBC = "org.springframework.boot:spring-boot-starter-jdbc:$version"
        const val WEB = "org.springframework.boot:spring-boot-starter-web:$version"
        const val JPA = "org.springframework.boot:spring-boot-starter-data-jpa:$version"
        const val VALIDATION = "org.springframework.boot:spring-boot-starter-validation:$version"
        const val DEV_TOOLS = "org.springframework.boot:spring-boot-devtools:$version"
    }

    const val LOMBOK = "org.projectlombok:lombok:1.18.8"
    const val HUTOOL = "cn.hutool:hutool-all:5.7.0"
    const val MYSQL = "mysql:mysql-connector-java:8.0.16"
    const val XML_BIND = "javax.xml.bind:jaxb-api:2.3.0"
    const val JWT = "io.jsonwebtoken:jjwt:0.9.0"
}