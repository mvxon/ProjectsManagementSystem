package com.strigalev.projectsservice.security;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;


@Slf4j
public class UserIdFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String URI = request.getRequestURI();
        if (URI.contains("/api/v1/users/")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Long userId = Long.parseLong(request.getHeader("X-auth-user-id"));
            User user = userService.getUserById(userId);
            UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString())
                    ));
            upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(upat);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("MISSING ID HEADER");
        }

    }

}
