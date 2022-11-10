package com.strigalev.authenticationservice.feign;

import com.strigalev.starter.dto.ApiResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "projects-service", path = "/api/v1/users/userDetails")
public interface FeignClientService {

    @GetMapping("/{email}")
    ApiResponseEntity getUserDetailsByEmail(@PathVariable String email);
}
