package site.javaghost.conolja.common.response;

import lombok.Builder;

@Builder
public record CustomSimpleErrors(
  String fieldName,
  Object rejectedValue,
  String message
) {

  public static CustomSimpleErrors create(String fieldName, Object rejectedValue, String message) {
    return CustomSimpleErrors.builder()
      .fieldName(fieldName)
      .rejectedValue(rejectedValue)
      .message(message)
      .build();
  }
}
