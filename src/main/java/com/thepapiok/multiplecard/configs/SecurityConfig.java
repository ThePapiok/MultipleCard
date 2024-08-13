package com.thepapiok.multiplecard.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user1 =
        User.builder().username("Admin").password("{noop}Admin").roles("ADMIN").build();
    UserDetails user2 =
        User.builder().username("User").password("{noop}User").roles("USER").build();
    UserDetails user3 =
        User.builder().username("Shop").password("{noop}Shop").roles("SHOP").build();
    return new InMemoryUserDetailsManager(user1, user2, user3);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    String loginUrl = "/login";
    http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
        .formLogin(
            login ->
                login
                    .loginPage(loginUrl)
                    .successForwardUrl("/")
                    .usernameParameter("login")
                    .passwordParameter("password"))
        .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl(loginUrl));
    return http.build();
  }
}
