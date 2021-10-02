package top.aengus.panther;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

@EntityScan(basePackages = "top.aengus.panther.model")
@EnableAsync
@SpringBootApplication
public class PantherApplication {

    public static void main(String[] args) {
        SpringApplication.run(PantherApplication.class, args);
    }

}
