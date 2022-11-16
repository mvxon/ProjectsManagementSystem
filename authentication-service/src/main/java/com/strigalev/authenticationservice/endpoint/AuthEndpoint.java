package com.strigalev.authenticationservice.endpoint;

import com.strigalev.authenticationservice.dto.SignInDTO;
import com.strigalev.starter.dto.TokenDTO;
import com.strigalev.authenticationservice.service.UserService;
import com.strigalev.starter.dto.UserDTO;
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

    @GetMapping("/logout")
    public void logout(@RequestParam(name = "refreshToken") String refreshToken) {
        userService.logout(refreshToken);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<TokenDTO> updateRefreshToken(@RequestParam(name = "refreshToken") String refreshToken) {
        return ResponseEntity.ok(userService.updateRefreshToken(refreshToken));
    }

    @GetMapping("/validateToken")
    public UserDTO validateToken(@RequestParam(name = "token") String accessToken) {
        return userService.validateAccessToken(accessToken);
    }
}
