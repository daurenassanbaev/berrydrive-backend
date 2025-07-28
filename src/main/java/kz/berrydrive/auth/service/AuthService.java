package kz.berrydrive.auth.service;

import kz.berrydrive.auth.dto.request.RegisterRequestDto;
import kz.berrydrive.auth.dto.request.SignInRequestDto;
import kz.berrydrive.auth.dto.response.AuthResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthResponseDto signIn(SignInRequestDto signInRequestDto, HttpServletResponse response);
    AuthResponseDto register(RegisterRequestDto registerRequestDto, HttpServletResponse response);
    AuthResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);
}
