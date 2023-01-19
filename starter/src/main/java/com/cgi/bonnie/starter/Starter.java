package com.cgi.bonnie.starter;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.cgi.bonnie.configuration"})
@SpringBootApplication
@EnableEncryptableProperties
public class Starter {

    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
    }

}
