package com.strigalev.apigateway.config;


import com.strigalev.apigateway.filter.AuthFilter;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {
    private static final String PATH = "/projects-service/api/v1/";

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, AuthFilter authFilter) {
        return builder.routes()
                .route("projects-service-route", r -> r
                        .path(
                                PATH + "projects/**",
                                PATH + "tasks/**",
                                PATH + "employees/**"
                        )
                        .filters(f -> (f.stripPrefix(1)).filter(authFilter.apply(new AuthFilter.Config())))
                        .uri("lb://projects-service/")
                )
                .route("userDetails-route", r -> r
                        .path(PATH + "users/userDetails/**")
                        .filters(f -> f.setStatus(HttpStatus.NOT_FOUND))
                        .uri("lb://projects-service/")
                )
                .build();
    }

}
