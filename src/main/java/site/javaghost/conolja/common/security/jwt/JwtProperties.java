package site.javaghost.conolja.common.security.jwt;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Builder
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
  String secret,
  String prefix,
  String header,
  String issuer,
  String type,
  String algorithm,
  @DurationUnit(ChronoUnit.MILLIS)
  Duration expiration,
  Refresh refresh) {
  @Builder
  public record Refresh(
    @DurationUnit(ChronoUnit.MILLIS)
    Duration expiration,
    String header) {
  }
}
