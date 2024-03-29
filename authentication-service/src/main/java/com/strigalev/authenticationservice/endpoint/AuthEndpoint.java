package com.strigalev.authenticationservice.endpoint;

import com.strigalev.authenticationservice.dto.ResetPasswordDTO;
import com.strigalev.authenticationservice.dto.SignInDTO;
import com.strigalev.authenticationservice.dto.SignUpRequest;
import com.strigalev.authenticationservice.service.UserService;
import com.strigalev.starter.dto.TokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthEndpoint {
    private final UserService userService;

    @PostMapping("/signIn")
    public ResponseEntity<TokenDTO> signIn(@RequestBody @Valid SignInDTO signInDTO) {
        return ResponseEntity.ok(userService.signIn(signInDTO));
    }

    @GetMapping("/logout")
    public void logout(@RequestParam(name = "refreshToken") String refreshToken) {
        userService.logout(refreshToken);
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<TokenDTO> updateRefreshToken(@RequestParam(name = "refreshToken") String refreshToken) {
        return ResponseEntity.ok(userService.updateRefreshToken(refreshToken));
    }

    @PostMapping("/signUp")
    public ResponseEntity<TokenDTO> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        return ResponseEntity.ok(userService.signUp(signUpRequest));
    }

    @GetMapping("/resetPassword/{email}")
    public void getAccessCodeForPasswordResetting(@PathVariable String email) {
        userService.createAndSendAccessCode(email);
    }

    @PostMapping("/resetPassword")
    public void resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO);
    }

}