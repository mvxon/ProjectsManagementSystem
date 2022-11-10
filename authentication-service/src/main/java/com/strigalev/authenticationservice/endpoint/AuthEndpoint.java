package com.strigalev.authenticationservice.endpoint;

import com.strigalev.authenticationservice.dto.SignInDTO;
import com.strigalev.starter.dto.TokenDTO;
import com.strigalev.authenticationservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthEndpoint {
    private final UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid SignInDTO signInDTO) {
        return ResponseEntity.ok(userService.login(signInDTO));
    }

    @PostMapping("/logout")
    public void logout(@RequestBody @Valid TokenDTO tokenDTO) {
        userService.logout(tokenDTO);
    }


    @PostMapping("/access-token")
    public ResponseEntity<TokenDTO> updateAccessToken(@RequestBody @Valid TokenDTO tokenDTO) {
        return ResponseEntity.ok(userService.updateAccessToken(tokenDTO));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDTO> updateRefreshToken(@RequestBody @Valid TokenDTO tokenDTO) {
        return ResponseEntity.ok(userService.updateRefreshToken(tokenDTO));
    }

    @PostMapping("/validateToken")
    public TokenDTO validateToken(@RequestParam(name = "token") String token) {
        return userService.validateToken(token);
    }
}
