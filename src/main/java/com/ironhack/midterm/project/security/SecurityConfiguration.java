package com.ironhack.midterm.project.security;

import com.ironhack.midterm.project.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable();
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.PUT, "/accounts/transfer").hasRole("ACCOUNTHOLDER")
                .mvcMatchers(HttpMethod.PUT, "/accounts/third-party").permitAll()
                .mvcMatchers(HttpMethod.PUT, "/accounts/**").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.GET, "/accounts/**").hasAnyRole("ADMIN", "ACCOUNTHOLDER")
                .mvcMatchers(HttpMethod.POST, "/accounts").hasRole("ADMIN")
                .anyRequest().permitAll();

    }
}
