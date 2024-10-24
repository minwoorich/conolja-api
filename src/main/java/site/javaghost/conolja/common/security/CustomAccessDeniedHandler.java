package site.javaghost.conolja.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import site.javaghost.conolja.common.exception.ErrorCode;
import site.javaghost.conolja.common.response.CustomErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  private final ObjectMapper objectMapper;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
    log.error("CustomAccessDeniedHandler: ", e);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/plain; charset=UTF-8");
    Object errorResponse = CustomErrorResponse.withOutDetails(
      request.getRequestURI(), ErrorCode.UNAUTHORIZED, LocalDateTime.now());
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
