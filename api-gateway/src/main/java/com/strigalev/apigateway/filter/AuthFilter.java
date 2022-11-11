package com.strigalev.apigateway.filter;

import com.strigalev.apigateway.feign.FeignClientService;
import com.strigalev.starter.dto.TokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final FeignClientService feignClientService;
    public AuthFilter(@Lazy FeignClientService feignClientService) {
        super(Config.class);
        this.feignClientService = feignClientService;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {

        ServerHttpResponse response = exchange.getResponse();
        log.error(err);

        response.setStatusCode(httpStatus);

        return response.setComplete();

    }

     @Override
         public GatewayFilter apply(Config config) {
             return (exchange, chain) -> {
                 ServerHttpRequest request = exchange.getRequest();

                 if (!request.getHeaders().containsKey("Authorization")) {
                     return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
                 }
                 ;

                 String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                 log.info(request.getURI() + " " + request.getPath().value());

                 String[] parts = authHeader.split(" ");

                 if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                     return this.onError(exchange, "Incorrect authorization structure", HttpStatus.UNAUTHORIZED);
                 }
                 try {
                     TokenDTO tokenDTO = feignClientService.validateToken(parts[1]);
                 } catch (Exception e) {
                     return this.onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                 }


            /* return webClientBuilder.build()
                    .post()
                    .uri("http://authentication-service/api/v1/auth/validateToken?token=" + parts[1])
                    .retrieve().bodyToMono(TokenDTO.class)
                    .map(tokenDTO -> {
                        exchange.getRequest();
                        return exchange;
                    }).flatMap(chain::filter);*/
                return chain.filter(exchange);
            };
    }
   /* @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new RuntimeException("Missing authorization information");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            String[] parts = authHeader.split(" ");

            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                throw new RuntimeException("Incorrect authorization structure");
            }

            return webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8082/authentication-service/api/v1/validateToken?token=" + parts[1])
                    .retrieve().bodyToMono(TokenDTO.class)
                    .map(tokenDTO -> {
                        exchange.getRequest()
                                .mutate()
                                .header("X-auth-user-id", String.valueOf(tokenDTO.getUserId()));
                        return exchange;
                    }).flatMap(chain::filter);
        };
    }*/


    public static class Config {
        // empty class as I don't need any particular configuration
    }
}
