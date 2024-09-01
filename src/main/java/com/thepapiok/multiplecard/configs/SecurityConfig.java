package com.thepapiok.multiplecard.configs;

import com.thepapiok.multiplecard.misc.CustomAuthenticationFailureHandler;
import com.thepapiok.multiplecard.misc.CustomAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, CustomAuthenticationFailureHandler customAuthenticationFailureHandler)
      throws Exception {
    String loginUrl = "/login";
    http.authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(HttpMethod.POST, "/reviews")
                    .hasRole("USER")
                    .anyRequest()
                    .permitAll())
        .formLogin(
            login ->
                login
                    .loginPage(loginUrl)
                    .loginProcessingUrl(loginUrl)
                    .usernameParameter("phone")
                    .passwordParameter("password")
                    .failureHandler(customAuthenticationFailureHandler))
        .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl(loginUrl))
        .csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authManager(
      HttpSecurity http, CustomAuthenticationProvider customAuthenticationProvider)
      throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
    return authenticationManagerBuilder.build();
  }
}
