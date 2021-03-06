package com.oschain.fastchaindb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages= {"com.oschain.fastchaindb.*","com.hyperledger.fabric.*"})
public class FabricApplication {

    public static void main(String[] args) {
        SpringApplication.run(FabricApplication.class, args);
    }

}
