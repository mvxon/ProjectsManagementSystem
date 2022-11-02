package com.strigalev.projectsservice.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${application.description}") String appDescription,
                                 @Value("${application.version}") String appVersion,
                                 @Value("${application.title}") String appTitle) {

        return new OpenAPI()
                .info(new Info()
                        .title(appTitle)
                        .version(appVersion)
                        .description(appDescription)
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new Contact()
                                .name("mvxon")
                                .email("morentetawer@gmail.com")
                                .url("https://github.com/mvxon")
                        )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));

    }
}
