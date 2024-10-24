package site.javaghost.conolja.common.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import site.javaghost.conolja.common.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CustomErrorResponse(
  String path,
  ErrorCode errorCode,
  LocalDateTime timestamp,
  String message,
  Object detail
) {

  public static CustomErrorResponse withDetails(String path, @NotNull ErrorCode errorCode, LocalDateTime timestamp, Object detail) {
    return CustomErrorResponse.builder()
      .path(path)
      .errorCode(errorCode)
      .timestamp(timestamp)
      .message(errorCode.getMessage())
      .detail(detail)
      .build();
  }

  public static CustomErrorResponse withOutDetails(String path, ErrorCode errorCode, LocalDateTime timestamp) {
    return CustomErrorResponse.builder()
      .path(path)
      .errorCode(errorCode)
      .timestamp(timestamp)
      .message(errorCode.getMessage())
      .detail(null)
      .build();
  }

  public static CustomErrorResponse withBindingResult(String path, ErrorCode errorCode, LocalDateTime timestamp, BindingResult bindingResult) {
    List<CustomSimpleErrors> errorList = getErrorList(bindingResult.getAllErrors());
    return CustomErrorResponse.builder()
      .path(path)
      .errorCode(errorCode)
      .timestamp(timestamp)
      .message(errorCode.getMessage())
      .detail(errorList)
      .build();
  }

  private static List<CustomSimpleErrors> getErrorList(List<ObjectError> errors) {
    return errors.stream()
      .filter(err -> err instanceof FieldError)
      .map(err -> (FieldError) err)
      .map(fieldError -> CustomSimpleErrors.create(
        fieldError.getField(),
        fieldError.getRejectedValue(),
        fieldError.getDefaultMessage()
      ))
      .toList();
  }
}
