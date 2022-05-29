package com.luxoft.naceapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@ComponentScan({"com.luxoft.naceapplication*"})
@EntityScan("com.luxoft.naceapplication*")
public class NaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NaceApplication.class , args);
    }

}
