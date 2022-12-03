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
    private static final String PROJECTS_SERVICE_PATH = "/projects-service/api/v1/";
    private static final String AUDIT_UNIT_PATH = "/audit-unit/api/v1/";
    private static final String REPORT_SERVICE_PATH = "/report-service/api/v1/";

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, AuthFilter authFilter) {
        return builder.routes()
                .route("projects-service-route", route -> route
                        .path(
                                PROJECTS_SERVICE_PATH + "projects/**",
                                PROJECTS_SERVICE_PATH + "tasks/**",
                                PROJECTS_SERVICE_PATH + "employees/**",
                                AUDIT_UNIT_PATH + "audit/**"
                        )
                        .filters(filter ->
                                (filter.stripPrefix(1)).filter(authFilter.apply(new AuthFilter.Config())))
                        .uri("lb://projects-service/")
                )
                .route("audit-unit-route", route -> route
                        .path(AUDIT_UNIT_PATH + "audit/**")
                        .filters(filter -> filter.setStatus(HttpStatus.NOT_FOUND))
                        .uri("lb://audit-unit/")
                )
                .route("report-service-route", route -> route
                        .path(REPORT_SERVICE_PATH + "reports/**")
                        .filters(filter ->
                                (filter.stripPrefix(1)).filter(authFilter.apply(new AuthFilter.Config())))
                        .uri("lb://report-service/")
                )
                .build();
    }

}
