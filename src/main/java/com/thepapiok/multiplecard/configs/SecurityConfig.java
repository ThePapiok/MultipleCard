package com.thepapiok.multiplecard.configs;

import com.thepapiok.multiplecard.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                    .loginProcessingUrl(loginUrl)
                    .usernameParameter("phone")
                    .passwordParameter("password"))
        .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl(loginUrl))
        .csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(UserService userService) {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(userService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return daoAuthenticationProvider;
  }
}
