package com.oschain.fastchaindb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages= "com.oschain.fastchaindb.*")
public class EthApplication {

    public static void main(String[] args) {
        SpringApplication.run(EthApplication.class, args);
    }

}
