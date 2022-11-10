package com.strigalev.apigateway.filter;

import com.strigalev.apigateway.feign.FeignClientService;
import com.strigalev.starter.dto.TokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final FeignClientService feignClientService;

    public AuthFilter(@Lazy FeignClientService feignClientService) {
        super(Config.class);
        this.feignClientService = feignClientService;
    }

    @Override
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

            if(feignClientService.validateToken(parts[1]).getUserId() == null) {
                throw new RuntimeException("Invalid token");
            }

      /*      return webClientBuilder.build()
                    .post()
                    .uri("http://authentication-service/api/v1/auth/validateToken?token=" + parts[1])
                    .retrieve().bodyToMono(TokenDTO.class)
                    .map(tokenDTO -> {
                        exchange.getRequest();
                        return exchange;
                    }).flatMap(chain::filter);*/
            exchange.getRequest().mutate();
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // empty class as I don't need any particular configuration
    }
}
