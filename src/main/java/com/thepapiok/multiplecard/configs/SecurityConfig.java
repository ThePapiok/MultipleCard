package com.thepapiok.multiplecard.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    String loginUrl = "/login";
    http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
        .formLogin(
            login ->
                login
                    .loginPage(loginUrl)
                    .successForwardUrl("/")
                    .usernameParameter("phone")
                    .passwordParameter("password"))
        .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl(loginUrl)).csrf(
                    AbstractHttpConfigurer::disable
            );
    return http.build();
  }
}
