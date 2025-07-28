package kz.berrydrive.auth.controller;

import kz.berrydrive.auth.dto.request.RegisterRequestDto;
import kz.berrydrive.auth.dto.request.SignInRequestDto;
import kz.berrydrive.auth.dto.response.AuthResponseDto;
import kz.berrydrive.auth.service.AuthService;
import kz.berrydrive.common.constant.RestEndpointPrefixes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestEndpointPrefixes.API + "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto, HttpServletResponse response) {
        return ResponseEntity.ok(authService.register(registerRequestDto, response));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponseDto> signIn(@RequestBody SignInRequestDto signInRequestDto, HttpServletResponse response) {
        return ResponseEntity.ok(authService.signIn(signInRequestDto, response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.refreshToken(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.noContent().build();
    }
}
