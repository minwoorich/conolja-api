package site.javaghost.conolja.common.security.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.javaghost.conolja.common.exception.ErrorCode;
import site.javaghost.conolja.common.exception.JwtAuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class JwtTokenUtilTest {

  @Test
  @DisplayName("헤더로 전달 받은 토큰에서 prefix 를 제거하고 토큰값만 파싱한다.")
  void parseToken() {
    // given
    String headerValue = "Bearer tokenValue";
    JwtProperties props = JwtProperties.builder().prefix("Bearer").build();
    JwtTokenUtil jwtTokenUtil = new JwtTokenUtil(props);

    // when
    String token = jwtTokenUtil.parseToken(headerValue);

    // then
    assertThat(token).isEqualTo("tokenValue");
  }

  @Test
  @DisplayName("Authorization 헤더 값의 prefix 가 틀릴 경우 예외를 발생시킨다.")
  void parseToken_ex1() {
    // given
    String headerValue = "Bearer!@# tokenValue";
    JwtProperties props = JwtProperties.builder().prefix("Bearer").build();
    JwtTokenUtil jwtTokenUtil = new JwtTokenUtil(props);

    // when
    Exception ex = catchException(() -> jwtTokenUtil.parseToken(headerValue));

    // then
    assertThat(ex)
      .isInstanceOf(JwtAuthenticationException.class)
      .extracting("errorCode")
      .isEqualTo(ErrorCode.JWT_INVALID_TYPE);

  }

  @Test
  @DisplayName("Authorization 헤더 값이 없는 경우 예외를 발생시킨다.")
  void parseToken_ex2() {
    // given
    String headerValue = "";
    JwtProperties props = JwtProperties.builder().prefix("Bearer").build();
    JwtTokenUtil jwtTokenUtil = new JwtTokenUtil(props);

    // when
    Exception ex = catchException(() -> jwtTokenUtil.parseToken(headerValue));

    // then
    assertThat(ex)
      .isInstanceOf(JwtAuthenticationException.class)
      .extracting("errorCode")
      .isEqualTo(ErrorCode.JWT_INVALID_AUTHORIZATION_HEADER);
  }
}