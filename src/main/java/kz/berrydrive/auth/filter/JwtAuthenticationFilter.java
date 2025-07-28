package kz.berrydrive.auth.filter;

import kz.berrydrive.auth.enums.TokenType;
import kz.berrydrive.auth.service.CustomUserDetailsService;
import kz.berrydrive.auth.service.jwt.JwtService;
import kz.berrydrive.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        SecurityContext context = SecurityContextHolder.getContext();

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX) || context.getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(7);
        String username = jwtService.extractSubject(jwtToken);

        if (username != null &&
                jwtService.isTokenValid(jwtToken) &&
                jwtService.extractTokenType(jwtToken).equals(TokenType.ACCESS_TOKEN)) {

            User user = (User) customUserDetailsService.loadUserByUsername(username);
            var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetails(request));
            context.setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
