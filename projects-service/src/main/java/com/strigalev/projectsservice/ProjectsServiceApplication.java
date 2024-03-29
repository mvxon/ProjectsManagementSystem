package com.strigalev.projectsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication(scanBasePackages = {"com.strigalev.starter", "com.strigalev.projectsservice"})
public class ProjectsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectsServiceApplication.class, args);
    }

}