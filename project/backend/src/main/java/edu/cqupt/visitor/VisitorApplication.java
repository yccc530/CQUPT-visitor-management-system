package edu.cqupt.visitor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("edu.cqupt.visitor.mapper")
public class VisitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(VisitorApplication.class, args);
    }
}
