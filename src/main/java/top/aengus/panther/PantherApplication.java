package top.aengus.panther;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@EntityScan(basePackages = "top.aengus.panther.model")
@SpringBootApplication
public class PantherApplication {

    public static void main(String[] args) {
        SpringApplication.run(PantherApplication.class, args);
    }

}
