package com.dvmrt.sse.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
            "br.com.ottimizza.filetransfer.controllers",
            "br.com.ottimizza.filetransfer.services",
            "br.com.ottimizza.filetransfer.configuration"
        }
)
public class SSEApplication {

    public static void main(String[] args) {
        SpringApplication.run(SSEApplication.class, args);
    }
}
