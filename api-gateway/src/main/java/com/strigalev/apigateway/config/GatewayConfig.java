package com.strigalev.apigateway.config;


import com.strigalev.apigateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class GatewayConfig {
    private static final String PROJECTS_SERVICE_PATH = "/projects-service/api/v1/";
    private static final String AUDIT_UNIT_PATH = "/audit-unit/api/v1/";
    private static final String REPORT_SERVICE_PATH = "/report-service/api/v1/";

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, GatewayFilter gatewayFilter) {
        return builder.routes()
                .route("projects-service-route", route -> route
                        .path(
                                PROJECTS_SERVICE_PATH + "projects/**",
                                PROJECTS_SERVICE_PATH + "tasks/**",
                                PROJECTS_SERVICE_PATH + "employees/**",
                                AUDIT_UNIT_PATH + "audit/**"
                        )
                        .filters(filter ->
                                (filter.stripPrefix(1)).filter(gatewayFilter.apply(new GatewayFilter.Config())))
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
                                (filter.stripPrefix(1)).filter(gatewayFilter.apply(new GatewayFilter.Config())))
                        .uri("lb://report-service/")
                )
                .build();
    }

}
