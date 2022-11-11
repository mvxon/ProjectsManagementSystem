package com.strigalev.apigateway.config;

import com.strigalev.apigateway.filter.AuthFilter;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class Config {
    /*@Bean
    public RouteLocator routeLocator(RouteLocatorBuilder rlb, AuthFilter authFilter) {
        return rlb
                .routes()
                .route(r -> r
                        .path("/projects-service/api/v1/projects/**", "/projects-service/api/v1/tasks/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                        .uri("lb://projects-service")
                )
                .build();
    }*/


    @Bean
    public Decoder decoder() {
        return new JacksonDecoder();
    }

    @Bean
    public Encoder encoder() {
        return new JacksonEncoder();
    }
}
