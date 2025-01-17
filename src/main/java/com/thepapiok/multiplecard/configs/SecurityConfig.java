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
    final String loginUrl = "/login";
    final String roleUser = "USER";
    final String roleAdmin = "ADMIN";
    http.authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(
                        HttpMethod.POST,
                        "/reviews/*",
                        "/buy_for_points",
                        "/find_nearest",
                        "/report")
                    .hasRole(roleUser)
                    .requestMatchers(HttpMethod.DELETE, "/reviews")
                    .hasRole(roleUser)
                    .requestMatchers("/new_card", "/block_card", "/order_card")
                    .hasRole(roleUser)
                    .requestMatchers(HttpMethod.POST, "/user")
                    .hasAnyRole(roleUser, roleAdmin)
                    .requestMatchers("/register", "/password_reset", "/register_shop")
                    .anonymous()
                    .requestMatchers("/password_change", "/delete_account", "/edit_profile")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/profile")
                    .authenticated()
                    .requestMatchers(
                        "/add_product",
                        "/promotions",
                        "/block_product",
                        "/unblock_product",
                        "/orders",
                        "/check_credentials",
                        "/check_pin",
                        "/finish_order")
                    .hasRole("SHOP")
                    .requestMatchers(
                        "/admin_panel",
                        "/change_user",
                        "/delete_product",
                        "/block_user",
                        "/delete_review",
                        "/mute_user",
                        "/categories",
                        "/delete_category",
                        "/reports",
                        "/reject_report",
                        "/block_at_report",
                        "/delete_and_block")
                    .hasRole(roleAdmin)
                    .requestMatchers("/products")
                    .hasAnyRole("SHOP", roleAdmin)
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
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(exception -> exception.accessDeniedPage("/access_denied"));
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
