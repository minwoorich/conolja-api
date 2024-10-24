package site.javaghost.conolja.common.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import site.javaghost.conolja.common.security.jwt.JwtProperties;
import site.javaghost.conolja.common.security.jwt.JwtTokenUtil;

@TestConfiguration
@RequiredArgsConstructor
public class JwtTestConfig {

  @Bean
  public JwtTokenUtil jwtTokenUtil(JwtProperties jwtProperties) {
    return new JwtTokenUtil(jwtProperties);
  }

  @Bean
  public JwtProperties jwtProperties() {
    return JwtProperties.builder()
      .secret("testSecret")
      .build();
  }
}
