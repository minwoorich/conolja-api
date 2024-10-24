package site.javaghost.conolja.common.security.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
@Valid
public record LoginRequest(
  @Email @Schema(description = "사용자명", example = "test@email.com")
  String username,
  @Schema(description = "사용자명", example = "1234")
  String password
) {
}
