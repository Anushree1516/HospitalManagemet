package com.bloodbank.bloodbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BloodbankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BloodbankApplication.class, args);
        System.out.println("🩸 Blood Bank Management System Started Successfully!");
        System.out.println("🚀 Server running at: http://localhost:8080");
        System.out.println("📌 API Base URL: http://localhost:8080/api/v1");
    }
}
