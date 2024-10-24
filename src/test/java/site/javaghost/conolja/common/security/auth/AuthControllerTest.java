package site.javaghost.conolja.common.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import site.javaghost.conolja.common.exception.ExceptionHandlerAdvice;
import site.javaghost.conolja.common.security.config.JwtTestConfig;
import site.javaghost.conolja.common.security.jwt.JwtProperties;
import site.javaghost.conolja.common.security.jwt.JwtTokenUtil;
import site.javaghost.conolja.domains.account.presentation.dto.AccountCreateRequest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = AuthController.class)
@Import({ExceptionHandlerAdvice.class, JwtTestConfig.class})
class AuthControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @MockBean
  AuthService authService;
  @MockBean
  JwtTokenUtil jwtTokenUtil;
  @MockBean
  JwtProperties jwtProperties;

  @DisplayName("POST /auth/signup: 회원가입")
  @WithMockUser
  @Test
  void signup() throws Exception {

    // given
    AccountCreateRequest request = AccountCreateRequest.builder()
      .email("test@email.com")
      .name("홍길동")
      .password("1234")
      .build();

    MockHttpServletRequestBuilder requestBuilder = post("/auth/signup")
      .contentType(MediaType.APPLICATION_JSON).with(csrf())
      .content(objectMapper.writeValueAsBytes(request))
      .accept(MediaType.APPLICATION_JSON);

    // when
    ResultActions resultActions = mockMvc.perform(requestBuilder);

    // then
    resultActions.andDo(print()).andExpect(status().isOk());
  }

  @Test
  @DisplayName("POST /auth/signup: 회원가입_유효성 검사 실패시 400")
  @WithMockUser
  void signup_ex() throws Exception {
    // given
    AccountCreateRequest request = AccountCreateRequest.builder()
      .email("testemail.com")
      .name("홍길동")
      .password("1234")
      .build();

    MockHttpServletRequestBuilder requestBuilder = post("/auth/signup")
      .contentType(MediaType.APPLICATION_JSON).with(csrf())
      .content(objectMapper.writeValueAsBytes(request))
      .accept(MediaType.APPLICATION_JSON);

    // when
    ResultActions resultActions = mockMvc.perform(requestBuilder);

    // then
    resultActions.andDo(print()).andExpect(status().isBadRequest());
  }
}