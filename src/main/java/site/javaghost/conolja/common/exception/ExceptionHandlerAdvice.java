package site.javaghost.conolja.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.javaghost.conolja.common.response.CustomErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {


  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public CustomErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
    log.error("ExceptionHandlerAdvice: ", e);
    return CustomErrorResponse.withBindingResult(
      request.getRequestURI(),
      ErrorCode.BAD_REQUEST,
      LocalDateTime.now(),
      e.getBindingResult());
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(JwtAuthenticationException.class)
  public CustomErrorResponse handleJwtAuthenticationException(JwtAuthenticationException e, HttpServletRequest request) {
    log.error("ExceptionHandlerAdvice: ", e);
    return CustomErrorResponse.withOutDetails(
      request.getRequestURI(),
      e.getErrorCode(),
      e.getTimestamp());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(CustomLogicException.class)
  public CustomErrorResponse handleCustomLogicException(CustomLogicException e, HttpServletRequest request) {
    log.error("ExceptionHandlerAdvice: ", e);
    return CustomErrorResponse.withOutDetails(
      request.getRequestURI(),
      e.getErrorCode(),
      e.getTimestamp());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalStateException.class)
  public CustomErrorResponse handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
    log.error("ExceptionHandlerAdvice: ", e);
    return CustomErrorResponse.withOutDetails(
      request.getRequestURI(),
      ErrorCode.BAD_REQUEST,
      LocalDateTime.now());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public CustomErrorResponse handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
    log.error("ExceptionHandlerAdvice: ", e);
    return CustomErrorResponse.withOutDetails(
      request.getRequestURI(),
      ErrorCode.BAD_REQUEST,
      LocalDateTime.now());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public CustomErrorResponse handleInternalServerException(Exception e, HttpServletRequest request) {
    log.error("ExceptionHandlerAdvice: ", e);
    return CustomErrorResponse.withOutDetails(
      request.getRequestURI(),
      ErrorCode.INTERNAL_SERVER_ERROR,
      LocalDateTime.now());
  }
}
