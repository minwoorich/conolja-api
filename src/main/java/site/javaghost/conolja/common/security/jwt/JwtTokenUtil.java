package site.javaghost.conolja.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import site.javaghost.conolja.common.exception.JwtAuthenticationException;
import site.javaghost.conolja.common.security.AccountRole;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

  private final JwtProperties jwtProperties;

  public String parseToken(String headerValue) {
    // 헤더가 비어있는지 확인
    if (!StringUtils.hasText(headerValue)) {
      throw JwtAuthenticationException.invalidAuthorizationHeader(headerValue);
    }

    final String prefixWithSpace = jwtProperties.prefix() + " ";

    // 헤더가 Bearer 인지 아닌지 확인
    if (!headerValue.startsWith(prefixWithSpace)) {
      // 헤더 형식이 잘못된 경우 예외 처리
      throw JwtAuthenticationException.invalidType(headerValue);
    }
    // prefix 이후의 토큰 부분 추출
    return headerValue.substring(prefixWithSpace.length());
  }

  // JWT 에서 Claim 추출
  public String extractUsername(String accessToken) {
    return getClaims(accessToken).getBody().getSubject();
  }

  // JwtTokenDto 토큰 생성
  public JwtTokenDto generateToken(Authentication authentication) {
    // 사용자 정보 추출
    String username = authentication.getName();
    String authority = authentication.getAuthorities().stream().findFirst().get().getAuthority();

    // 액세스 토큰 생성
    String accessToken = generateAccessToken(username);

    // 리프레시 토큰 생성
    String refreshToken = generateRefreshToken();

    return JwtTokenDto.by(accessToken, refreshToken, authority);
  }

  private String generateRefreshToken() {
    Date date = new Date(System.currentTimeMillis() + jwtProperties.refresh().expiration().toMillis());
    return Jwts.builder()
      .setExpiration(date)
      .signWith(getPrivateKey(jwtProperties.secret()), SignatureAlgorithm.HS512)
      .compact();
  }

  private String generateAccessToken(String username) {
    Date date = new Date(System.currentTimeMillis() + jwtProperties.expiration().toMillis());
    return Jwts.builder()
      .setSubject(username)
      .setIssuer(jwtProperties.issuer())
      // 만료 시간 = 현재 시각 + 유효 기간
      .setExpiration(date)
      .setIssuedAt(new Date())
      .signWith(getPrivateKey(jwtProperties.secret()), SignatureAlgorithm.forName(jwtProperties.type()))
      .compact();
  }

  public JwtTokenDto reIssueToken(String refreshToken) {
    if (hasExpired(refreshToken)) {
      throw JwtAuthenticationException.tokenExpired();
    }

    String username = extractUsername(refreshToken);
    String authority = AccountRole.USER.name();

    String accessToken = generateAccessToken(username);

    return JwtTokenDto.by(accessToken, refreshToken, authority);
  }

  private SecretKeySpec getPrivateKey(String secretKey) {
    try {
      byte[] keyBytes = Base64.getDecoder().decode(secretKey);
      return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    } catch (IllegalArgumentException e) {
      throw JwtAuthenticationException.invalidSecretKey();
    }
  }

  public boolean hasExpired(String token) {
    return hasExpired(getClaims(token));
  }

  private Jws<Claims> getClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getPrivateKey(jwtProperties.secret()))
      .build()
      .parseClaimsJws(token);
  }

  private boolean hasExpired(Jws<Claims> claims) {
    return claims.getBody().getExpiration().before(new Date());
  }
}

