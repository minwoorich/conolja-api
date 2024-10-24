package site.javaghost.conolja.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import site.javaghost.conolja.common.exception.JwtAuthenticationException;
import site.javaghost.conolja.common.response.CustomErrorResponse;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
    log.error("JwtAuthenticationEntryPoint: ", e);
    ObjectMapper objectMapper = new ObjectMapper();
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json; charset=UTF-8");
    CustomErrorResponse errorResponse = null;
    if (e instanceof JwtAuthenticationException) {
      errorResponse = CustomErrorResponse.withOutDetails(
        request.getRequestURI(),
        ((JwtAuthenticationException) e).getErrorCode(),
        ((JwtAuthenticationException) e).getTimestamp());
    }
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
