package com.strigalev.projectsservice.security;

import com.strigalev.starter.jwt.JwtClaims;
import com.strigalev.starter.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = request.getHeader("Authorization");

            JwtClaims claims = jwtUtil.getJwtClaims(token);
            Long id = claims.getId();
            String role = claims.getRole();

            UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
                    id,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(role)
                    )
            );

            upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(upat);
            filterChain.doFilter(request, response);

        } catch (IllegalArgumentException | BadCredentialsException ex) {
            response.setStatus(403);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Invalid access token");
        }
    }

}
