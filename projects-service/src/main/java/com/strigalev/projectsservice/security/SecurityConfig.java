package com.strigalev.projectsservice.security;

import com.strigalev.projectsservice.service.UserService;
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

    private final static String PATH_PROJECTS = "/api/v1/projects";
    private static final String PATH_TASKS = "/api/v1/tasks";
    private final UserService userService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/api/v1/users/**",
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/**",
                "/swagger-ui/index.html",
                "/webjars/**"
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, PATH_PROJECTS + "/**")
                .hasAuthority(Role.ADMIN.name())

                .antMatchers(HttpMethod.POST, PATH_TASKS + "/**")
                .hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name())

                .antMatchers(HttpMethod.DELETE, PATH_PROJECTS + "/**")
                .hasAuthority(Role.ADMIN.name())

                .antMatchers(HttpMethod.DELETE, PATH_TASKS + "/**")
                .hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name())

                .antMatchers(HttpMethod.PATCH, PATH_PROJECTS + "/**")
                .hasAnyAuthority(Role.ADMIN.name())

                .antMatchers(HttpMethod.PUT, PATH_PROJECTS + "/**")
                .hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name())

                .antMatchers(HttpMethod.PUT, PATH_TASKS + "/**")
                .hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name())

                .antMatchers(PATH_TASKS + "/takeForTesting/**")
                .hasAnyAuthority(Role.TESTER.name(), Role.ADMIN.name(), Role.MANAGER.name())

                .antMatchers(
                        PATH_TASKS + "/setOpen/**",
                        PATH_TASKS + "/assignToEmployee/**",
                        PATH_TASKS + "/setDocumented/**",
                        PATH_PROJECTS + "/addEmployee/**"
                )
                .hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name())

                .antMatchers(PATH_TASKS + "/setDeveloping/**")
                .hasAuthority(Role.DEVELOPER.name())

                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new UserIdFilter(userService), UsernamePasswordAuthenticationFilter.class);
    }
}
