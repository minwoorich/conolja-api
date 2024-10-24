package site.javaghost.conolja.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import site.javaghost.conolja.common.response.CustomErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      //  UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;
      filterChain.doFilter(request, response);
    } catch (JwtAuthenticationException e) {
      log.error("ExceptionHandlerFilter: ", e);
      setErrorResponse(response, request.getRequestURI(), e.getErrorCode());
    } catch (UnsupportedJwtException e) {
      log.error("ExceptionHandlerFilter: ", e);
      setErrorResponse(response, request.getRequestURI(), ErrorCode.JWT_UNSUPPORTED_TOKEN);
    } catch (MalformedJwtException e) {
      log.error("ExceptionHandlerFilter: ", e);
      setErrorResponse(response, request.getRequestURI(), ErrorCode.JWT_INVALID_TOKEN);
    } catch (ExpiredJwtException e) {
      log.error("ExceptionHandlerFilter: ", e);
      setErrorResponse(response, request.getRequestURI(), ErrorCode.JWT_TOKEN_EXPIRED);
    } catch (Exception e) {
      // 서버 에러
      log.error("ExceptionHandlerFilter: ", e);
      setErrorResponse(response, request.getRequestURI(), ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  private void setErrorResponse(HttpServletResponse response, String path, ErrorCode errorCode) throws IOException {
    response.setStatus(errorCode.getStatus());
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json; charset=UTF-8");
    String body = objectMapper.writeValueAsString(
      CustomErrorResponse.withOutDetails(path, errorCode, LocalDateTime.now())
    );
    response.getWriter().write(body);
  }
}
