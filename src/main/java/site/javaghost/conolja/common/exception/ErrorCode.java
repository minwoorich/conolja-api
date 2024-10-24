package site.javaghost.conolja.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  BAD_REQUEST(400, "잘못된 요청입니다."),

  JWT_INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
  JWT_UNSUPPORTED_TOKEN(401, "지원하지 않는 JWT 토큰 유형입니다."),
  JWT_TOKEN_EXPIRED(401, "만료된 토큰입니다."),
  JWT_INVALID_AUTHORIZATION_HEADER(401, "헤더 정보가 유효하지 않습니다."),
  JWT_INVALID_ISSUER(401, "발급자 정보가 유효하지 않습니다."),
  JWT_INVALID_TYPE(401, "토큰 타입이 유효하지 않습니다."),
  JWT_INVALID_SECRET_KEY(401, "시크릿키가 유효하지 않습니다"),

  INVALID_PASSWORD(401, "비밀번호가 일치하지 않습니다."),
  INVALID_USERNAME(401, "유저네임이 일치하지 않습니다."),
  DUPLICATED_ACCOUNT(401, "이미 존재하는 계정입니다."),

  UNAUTHORIZED(403, "권한이 없습니다."),

  INTERNAL_SERVER_ERROR(500, "서버 내부 에러입니다.");

  private final int status;
  private final String message;
}

