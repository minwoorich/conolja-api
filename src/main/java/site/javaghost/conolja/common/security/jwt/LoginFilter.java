package site.javaghost.conolja.common.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import site.javaghost.conolja.common.exception.JwtAuthenticationException;
import site.javaghost.conolja.common.security.CustomUserDetails;
import site.javaghost.conolja.common.servlet.CustomRequestWrapper;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;
  private final AuthenticationManager authManager;

  private static final String LOGIN_URI = "/auth/login"; // 로그인 요청 URI


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if (!isLoginUri(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 인증 정보 생성
    try {
      // 인증 시도
      HttpServletRequest wrappedRequest = wrappedRequest(request);
      Authentication auth = attemptAuthentication(wrappedRequest);
      // 인증 정보가 있는 경우
      successfulAuthentication(wrappedRequest, response, filterChain, auth);
    } catch (UsernameNotFoundException e) {
      throw JwtAuthenticationException.usernameNotFound(e);
    } catch (BadCredentialsException e) {
      throw JwtAuthenticationException.badCredentials(e);
    } catch (RuntimeException e) {
      log.error("로그인 정보를 읽어오는데 실패했습니다.", e);
      throw e;
    }
  }

  protected HttpServletRequestWrapper wrappedRequest(HttpServletRequest request) {
    try {
      return new CustomRequestWrapper(request);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isLoginUri(HttpServletRequest request) {
    return request.getRequestURI().matches(LOGIN_URI);
  }

  public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
    LoginRequest loginInfo = getLoginInfo(request);

    // accessToken 을 이용해 인증 정보 생성 (아직 인증 전 단계)
    String username = loginInfo.username();
    String password = loginInfo.password();
    UsernamePasswordAuthenticationToken preAuthToken =
      UsernamePasswordAuthenticationToken.unauthenticated(CustomUserDetails.create(username, password, List.of()), password);

    // authenticationProvider 를 통해 검증 '된' 인증 정보 생성
    return authManager.authenticate(preAuthToken);
  }

  protected LoginRequest getLoginInfo(HttpServletRequest request) {
    try {
      return objectMapper.readValue(request.getInputStream(), LoginRequest.class);
    } catch (IOException e) {
      log.error("로그인 정보를 읽어오는데 실패했습니다.", e);
      throw new RuntimeException(e);
    }
  }

  protected void successfulAuthentication(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain,
    Authentication authResult) throws IOException, ServletException {
    // SecurityContextHolder 에 인증 정보 저장
    SecurityContextHolder.getContext().setAuthentication(authResult);
    chain.doFilter(request, response);
  }
}
