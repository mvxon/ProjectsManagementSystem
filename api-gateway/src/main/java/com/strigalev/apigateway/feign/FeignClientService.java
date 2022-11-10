package com.strigalev.apigateway.feign;

import com.strigalev.starter.dto.TokenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authentication-service", path = "/api/v1/auth")
public interface FeignClientService {

    @PostMapping("/validateToken")
    TokenDTO validateToken(@RequestParam(name = "token") String token);
}
