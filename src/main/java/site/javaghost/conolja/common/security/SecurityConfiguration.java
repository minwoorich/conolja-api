package site.javaghost.conolja.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.javaghost.conolja.common.exception.ExceptionHandlerFilter;
import site.javaghost.conolja.common.security.jwt.JwtValidationFilter;
import site.javaghost.conolja.common.security.jwt.LoginFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final JwtValidationFilter jwtValidationFilter;
  private final AuthenticationProvider jwtAuthenticationProvider;
  private final ObjectMapper objectMapper;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .csrf(AbstractHttpConfigurer::disable)
      .cors(AbstractHttpConfigurer::disable)
      .httpBasic(AbstractHttpConfigurer::disable)
      .formLogin(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()  // static resources 허용
        .requestMatchers("/auth/**").permitAll() // 인증 관련 엔드포인트
        .requestMatchers("/apis/**", "/swagger-ui/**", "/api-docs/**").permitAll() // 스웨거
        .requestMatchers("/error/**").permitAll() // 에러 페이지
        .anyRequest().authenticated()  // 나머지 경로는 인증 요구
      )
      .addFilterBefore(loginFilter(http, objectMapper), UsernamePasswordAuthenticationFilter.class)
      .addFilterBefore(jwtValidationFilter, LoginFilter.class)
      .addFilterBefore(exceptionHandlerFilter(), JwtValidationFilter.class)
      //커스텀 에러 핸들링
      .exceptionHandling(exception -> exception
        // 권한이 없을 때 (403) 커스텀 처리
        .accessDeniedHandler(accessDeniedHandler())
        // 인증되지 않은 사용자가 접근할 때 (401)
        .authenticationEntryPoint(authenticationEntryPoint())
      );

    return http.build();
  }

  @Bean
  public LoginFilter loginFilter(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
    return new LoginFilter(objectMapper, authenticationManager(http));
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
    builder.authenticationProvider(jwtAuthenticationProvider);
    return builder.build();
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return new JwtAuthenticationEntryPoint();
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return new CustomAccessDeniedHandler(objectMapper);
  }

  @Bean
  public ExceptionHandlerFilter exceptionHandlerFilter() {
    return new ExceptionHandlerFilter(objectMapper);
  }
}
