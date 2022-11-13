package com.strigalev.projectsservice.security;

import com.strigalev.starter.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserIdFilter userIdFilter() {
        return new UserIdFilter();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/v1/users/**");
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/projects")
                .hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.POST, "/api/v1/tasks")
                .hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name())
                .antMatchers(HttpMethod.DELETE, "/api/v1/projects/**")
                .hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/api/v1/tasks/**")
                .hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name())
                .antMatchers(HttpMethod.PUT, "/api/v1/projects/**")
                .hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name())
                .antMatchers(HttpMethod.PUT, "/api/v1/tasks/**")
                .hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name())
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(userIdFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
