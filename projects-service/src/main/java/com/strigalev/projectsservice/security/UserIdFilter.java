package com.strigalev.projectsservice.security;

import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
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


@Slf4j
public class UserIdFilter extends OncePerRequestFilter {


    private final UserService userService;

    public UserIdFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(request.getHeader("X-auth-user-id"));
            UserDTO user = userService.getUserDtoById(userId);
            UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString())
                    ));
            upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(upat);
            filterChain.doFilter(request, response);
        } catch (NumberFormatException e) {
            response.setStatus(403);
        }

    }

}
