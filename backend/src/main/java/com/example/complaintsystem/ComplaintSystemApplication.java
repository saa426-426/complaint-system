// backend/src/main/java/com/example/complaintsystem/ComplaintSystemApplication.java
package com.example.complaintsystem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
@SpringBootApplication
@EnableAsync
public class ComplaintSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComplaintSystemApplication.class, args);
    }
}
