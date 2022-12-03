package com.strigalev.reportservice.security;

import com.strigalev.starter.model.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;


@Slf4j
public class PostGatewayFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) {
        try {
            Role role = Role.valueOf(request.getHeader("X-auth-user-role"));
            UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
                    null,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(role.toString())
                    ));
            upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(upat);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(403);
        }

    }

}
